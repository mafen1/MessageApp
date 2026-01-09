package com.example.messageapp.domain.useCase

import androidx.datastore.preferences.core.Preferences
import com.example.messageapp.store.DataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppPreferencesUseCase @Inject constructor(private val dataStore: DataStore) {

    suspend fun <T> save(key: String, value: T) {
        dataStore.save(key, value)
    }
    fun <T> readValue(key: Preferences.Key<T>): Flow<T> {
        return dataStore.readValue(key, defaultValue = "" as T)
    }

    fun getString(key: String): Flow<String>{
        return dataStore.getStringValue(key)
    }

    suspend fun setString(key: String, value: String) {
        dataStore.setStringValue(key, value)
    }

}