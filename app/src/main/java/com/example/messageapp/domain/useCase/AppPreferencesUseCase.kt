package com.example.messageapp.domain.usecase

import com.example.messageapp.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow

class AppPreferencesUseCase constructor(
    private val preferencesRepository: PreferencesRepository
) {
    fun getString(key: String): Flow<String> = preferencesRepository.getString(key)

    suspend fun setString(key: String, value: String) {
        preferencesRepository.setString(key, value)
    }
}
