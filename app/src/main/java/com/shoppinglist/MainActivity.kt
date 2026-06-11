package com.shoppinglist

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shoppinglist.presentation.ShoppingViewModel
import com.shoppinglist.servise.ShoppingServise
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: ShoppingViewModel = hiltViewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            MaterialTheme(
                colorScheme = if (isDarkMode) darkColorScheme() else lightColorScheme()
            ) {
                ShoppingListApp()
            }
        }
    }
}

fun startShoppingService(context: android.content.Context) {
    val intent = android.content.Intent(context, ShoppingServise::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
    } else {
        context.startService(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListApp() {
    val viewModel: ShoppingViewModel = hiltViewModel()
    val items by viewModel.items.collectAsState()
    val languageCode by viewModel.languageCode.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var editingItem by remember { mutableStateOf<com.shoppinglist.core.domain.model.ShoppingItem?>(null) }
    var editName by remember { mutableStateOf("") }
    var editQuantity by remember { mutableStateOf("") }
    val context = LocalContext.current

    val isRu = languageCode == "ru"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isRu) "Список покупок" else "Shopping List") },
                actions = {
                    IconButton(onClick = { startShoppingService(context) }) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "За покупками")
                    }
                    IconButton(onClick = { showLanguageDialog = true }) {
                        Icon(Icons.Default.Language, contentDescription = "Language")
                    }
                    IconButton(onClick = { viewModel.toggleTheme() }) {
                        Icon(Icons.Default.DarkMode, contentDescription = "Theme")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = if (isRu) "Добавить" else "Add")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (items.isEmpty()) {
                Text(
                    text = if (isRu) "Список пуст. Нажмите + чтобы добавить товар"
                    else "List is empty. Press + to add item",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn {
                    items(items) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            textDecoration = if (item.isChecked) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                        )
                                    )
                                    Text(
                                        text = "${if (isRu) "Кол-во" else "Qty"}: ${item.quantity}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Row {
                                    IconButton(onClick = { viewModel.toggleItem(item.id) }) {
                                        Icon(
                                            imageVector = if (item.isChecked) Icons.Default.CheckCircle
                                            else Icons.Default.RadioButtonUnchecked,
                                            contentDescription = if (isRu) "Вычеркнуть" else "Cross off"
                                        )
                                    }
                                    IconButton(onClick = {
                                        editingItem = item
                                        editName = item.name
                                        editQuantity = item.quantity.toString()
                                        showEditDialog = true
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Редактировать")
                                    }
                                    IconButton(onClick = { viewModel.deleteItem(item) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = if (isRu) "Удалить" else "Delete"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (isRu) "Добавить товар" else "Add item") },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text(if (isRu) "Название" else "Name") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text(if (isRu) "Количество" else "Quantity") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val qty = quantity.toIntOrNull() ?: 1
                        if (itemName.isNotBlank()) {
                            viewModel.addItem(itemName, qty)
                            itemName = ""
                            quantity = "1"
                            showDialog = false
                        }
                    }
                ) {
                    Text(if (isRu) "Добавить" else "Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(if (isRu) "Отмена" else "Cancel")
                }
            }
        )
    }

    if (showEditDialog && editingItem != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(if (isRu) "Редактировать товар" else "Edit item") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text(if (isRu) "Название" else "Name") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editQuantity,
                        onValueChange = { editQuantity = it },
                        label = { Text(if (isRu) "Количество" else "Quantity") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val qty = editQuantity.toIntOrNull() ?: 1
                        if (editName.isNotBlank() && editingItem != null) {
                            viewModel.updateItem(editingItem!!.id, editName, qty)
                            showEditDialog = false
                        }
                    }
                ) {
                    Text(if (isRu) "Сохранить" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text(if (isRu) "Отмена" else "Cancel")
                }
            }
        )
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(if (isRu) "Выберите язык" else "Select language") },
            text = {
                Column {
                    TextButton(
                        onClick = {
                            viewModel.setLanguage("ru")
                            showLanguageDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Русский")
                    }
                    TextButton(
                        onClick = {
                            viewModel.setLanguage("en")
                            showLanguageDialog = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("English")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(if (isRu) "Отмена" else "Cancel")
                }
            }
        )
    }
}