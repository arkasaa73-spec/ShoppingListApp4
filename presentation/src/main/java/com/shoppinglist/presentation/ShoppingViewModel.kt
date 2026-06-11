package com.shoppinglist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoppinglist.core.domain.model.ShoppingItem
import com.shoppinglist.core.domain.repository.ShoppingRepository
import com.shoppinglist.core.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoppingViewModel @Inject constructor(
    private val shoppingRepository: ShoppingRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val items: StateFlow<List<ShoppingItem>> = shoppingRepository.getItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isDarkMode: StateFlow<Boolean> = settingsRepository.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val languageCode: StateFlow<String> = settingsRepository.languageCode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "ru")

    fun addItem(name: String, quantity: Int) {
        viewModelScope.launch {
            val newItem = ShoppingItem(
                id = 0,
                name = name,
                quantity = quantity,
                isChecked = false
            )
            shoppingRepository.insertItem(newItem)
        }
    }

    fun toggleItem(id: Int) {
        viewModelScope.launch {
            shoppingRepository.toggleItem(id)
        }
    }

    fun deleteItem(item: ShoppingItem) {
        viewModelScope.launch {
            shoppingRepository.deleteItem(item)
        }
    }

    fun updateItem(id: Int, newName: String, newQuantity: Int) {
        viewModelScope.launch {
            shoppingRepository.updateItem(id, newName, newQuantity)
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            settingsRepository.setDarkMode(!isDarkMode.value)
        }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch {
            settingsRepository.setLanguage(lang)
        }
    }
}