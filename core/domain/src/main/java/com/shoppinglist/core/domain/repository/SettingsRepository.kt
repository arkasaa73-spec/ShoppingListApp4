package com.shoppinglist.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val isDarkMode: Flow<Boolean>
    val languageCode: Flow<String>
    suspend fun setDarkMode(isDark: Boolean)
    suspend fun setLanguage(langCode: String)
}