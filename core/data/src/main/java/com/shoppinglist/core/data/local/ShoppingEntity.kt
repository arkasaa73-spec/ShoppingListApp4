package com.shoppinglist.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shoppinglist.core.domain.model.ShoppingItem

@Entity(tableName = "shopping_items")
data class ShoppingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val quantity: Int,
    val isChecked: Boolean = false
) {
    fun toDomain(): ShoppingItem = ShoppingItem(
        id = id,
        name = name,
        quantity = quantity,
        isChecked = isChecked
    )

    companion object {
        fun fromDomain(item: ShoppingItem): ShoppingEntity = ShoppingEntity(
            id = item.id,
            name = item.name,
            quantity = item.quantity,
            isChecked = item.isChecked
        )
    }
}