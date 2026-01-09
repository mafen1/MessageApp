package com.example.messageapp.domain.repo.preferences

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow


interface AppPreference {
    suspend fun <T> save(key: String, value: T)
    fun <T> readValue(key: Preferences.Key<T>, defaultValue: T ): Flow<T>

    suspend fun setStringValue(key: String,value: String)

    fun getStringValue(key: String): Flow<String>

}