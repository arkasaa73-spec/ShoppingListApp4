package com.shoppinglist.core.domain.repository

import com.shoppinglist.core.domain.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

interface ShoppingRepository {
    fun getItems(): Flow<List<ShoppingItem>>
    suspend fun insertItem(item: ShoppingItem)
    suspend fun toggleItem(id: Int)
    suspend fun deleteItem(item: ShoppingItem)
    suspend fun updateItem(id: Int, newName: String, newQuantity: Int)
}