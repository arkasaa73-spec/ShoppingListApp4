package com.shoppinglist.core.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.shoppinglist.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    companion object {
        private val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")
        private val LANGUAGE_CODE = stringPreferencesKey("language_code")
    }

    override val isDarkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[IS_DARK_MODE] ?: false
    }

    override val languageCode: Flow<String> = dataStore.data.map { preferences ->
        preferences[LANGUAGE_CODE] ?: "ru"
    }

    override suspend fun setDarkMode(isDark: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = isDark
        }
    }

    override suspend fun setLanguage(langCode: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_CODE] = langCode
        }
    }
}