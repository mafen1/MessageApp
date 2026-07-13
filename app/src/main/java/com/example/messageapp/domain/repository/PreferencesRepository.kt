package com.example.messageapp.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun getString(key: String): Flow<String>
    suspend fun setString(key: String, value: String)
}
