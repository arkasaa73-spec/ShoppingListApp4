package com.shoppinglist.core.domain.model

data class ShoppingItem(
    val id: Int,
    val name: String,
    val quantity: Int,
    val isChecked: Boolean = false
)