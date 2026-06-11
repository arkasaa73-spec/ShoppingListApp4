package com.shoppinglist.core.data.repository

import com.shoppinglist.core.data.local.ShoppingDao
import com.shoppinglist.core.data.local.ShoppingEntity
import com.shoppinglist.core.domain.model.ShoppingItem
import com.shoppinglist.core.domain.repository.ShoppingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ShoppingRepositoryImpl @Inject constructor(
    private val dao: ShoppingDao
) : ShoppingRepository {

    override fun getItems(): Flow<List<ShoppingItem>> {
        return dao.getAllItems().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertItem(item: ShoppingItem) {
        dao.insertItem(ShoppingEntity.fromDomain(item))
    }

    override suspend fun toggleItem(id: Int) {
        dao.toggleItem(id)
    }

    override suspend fun deleteItem(item: ShoppingItem) {
        dao.deleteItem(ShoppingEntity.fromDomain(item))
    }

    override suspend fun updateItem(id: Int, newName: String, newQuantity: Int) {
        dao.updateItem(id, newName, newQuantity)
    }
}