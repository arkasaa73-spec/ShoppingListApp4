package com.shoppinglist.core.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingDao {
    @Query("SELECT * FROM shopping_items")
    fun getAllItems(): Flow<List<ShoppingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingEntity)

    @Query("UPDATE shopping_items SET isChecked = NOT isChecked WHERE id = :id")
    suspend fun toggleItem(id: Int)

    @Delete
    suspend fun deleteItem(item: ShoppingEntity)

    @Query("UPDATE shopping_items SET name = :newName, quantity = :newQuantity WHERE id = :id")
    suspend fun updateItem(id: Int, newName: String, newQuantity: Int)
}