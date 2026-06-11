package com.shoppinglist.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.shoppinglist.core.data.local.AppDatabase
import com.shoppinglist.core.data.local.ShoppingDao
import com.shoppinglist.core.data.repository.SettingsRepositoryImpl
import com.shoppinglist.core.data.repository.ShoppingRepositoryImpl
import com.shoppinglist.core.domain.repository.SettingsRepository
import com.shoppinglist.core.domain.repository.ShoppingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "shopping_database"
        ).build()
    }

    @Provides
    fun provideShoppingDao(database: AppDatabase): ShoppingDao {
        return database.shoppingDao()
    }

    @Provides
    @Singleton
    fun provideShoppingRepository(dao: ShoppingDao): ShoppingRepository {
        return ShoppingRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("settings") }
        )
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(dataStore: DataStore<Preferences>): SettingsRepository {
        return SettingsRepositoryImpl(dataStore)
    }
}