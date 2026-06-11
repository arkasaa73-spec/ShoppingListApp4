package com.shoppinglist.localization

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object LanguageManager {
    private val _currentLanguage = MutableStateFlow("ru")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    fun setLanguage(lang: String) {
        _currentLanguage.value = lang
    }

    fun getString(ru: String, en: String): String {
        return if (_currentLanguage.value == "ru") ru else en
    }
}