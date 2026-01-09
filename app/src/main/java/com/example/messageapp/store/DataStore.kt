package com.example.messageapp.store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.messageapp.core.logD
import com.example.messageapp.domain.repo.preferences.AppPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStore @Inject constructor(@ApplicationContext private val context: Context) :
    AppPreference {

    override suspend fun <T> save(key: String, value: T) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value as String
        }
        logD(value.toString())
    }

    override fun <T> readValue(
        key: Preferences.Key<T>,
        defaultValue: T
    ): Flow<T> = context.dataStore.data.map { settings ->
        (settings[key] ?: defaultValue) as T
    }

    override suspend fun setStringValue(key: String, value: String) {
        save(key, value)
//        logD(value)
    }


    override fun getStringValue(key: String): Flow<String> {
//        logD(key)
        return readValue(stringPreferencesKey(key), "")
    }



}