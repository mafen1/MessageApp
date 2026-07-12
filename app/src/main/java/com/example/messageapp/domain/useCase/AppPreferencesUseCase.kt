package com.example.messageapp.domain.useCase

import androidx.datastore.preferences.core.Preferences
import com.example.messageapp.store.DataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AppPreferencesUseCase @Inject constructor(private val dataStore: DataStore) {

    suspend fun <T> save(key: String, value: T) {
        dataStore.save(key, value)
    }

    fun readString(key: Preferences.Key<String>): Flow<String> {
        return dataStore.readValue(key, defaultValue = "")
    }

    fun getString(key: String): Flow<String>{
        return dataStore.getStringValue(key)
    }

    suspend fun setString(key: String, value: String) {
        dataStore.setStringValue(key, value)
    }

}
