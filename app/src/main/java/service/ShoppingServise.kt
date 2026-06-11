package com.shoppinglist.servise

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.shoppinglist.core.domain.repository.ShoppingRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ShoppingServise : Service() {

    @Inject
    lateinit var repository: ShoppingRepository
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "TOGGLE_ITEM") {
            val itemId = intent.getIntExtra("ITEM_ID", -1)
            if (itemId != -1) {
                serviceScope.launch { repository.toggleItem(itemId) }
            }
        }
        createNotificationChannel()
        observeAndNotify()
        return START_STICKY
    }

    private fun observeAndNotify() {
        serviceScope.launch {
            repository.getItems().collect { items ->
                val nextItem = items.firstOrNull { !it.isChecked }
                if (nextItem == null) {
                    stopSelf()
                    return@collect
                }

                val toggleIntent = Intent(this@ShoppingServise, ShoppingServise::class.java).apply {
                    action = "TOGGLE_ITEM"
                    putExtra("ITEM_ID", nextItem.id)
                }

                val pendingIntent = PendingIntent.getService(
                    this@ShoppingServise, nextItem.id, toggleIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(this@ShoppingServise, "shopping_channel")
                    .setContentTitle("За покупками")
                    .setContentText("Купи: ${nextItem.name} (${nextItem.quantity} шт.)")
                    .setSmallIcon(android.R.drawable.ic_menu_agenda)
                    .addAction(android.R.drawable.checkbox_on_background, "OK", pendingIntent)
                    .setOngoing(true)
                    .build()

                startForeground(1, notification)
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "shopping_channel",
                "Shopping Mode",
                NotificationManager.IMPORTANCE_HIGH
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}