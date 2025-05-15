package com.example.messageapp.domain.useCase

import com.example.messageapp.store.SharedPreference
import javax.inject.Inject

class AppPreferencesUseCase @Inject constructor(private val sharedPreference: SharedPreference) {

    fun save(keyName: String, value: String) {
        sharedPreference.save(keyName, value)
    }
    fun getValueString(keyName: String): String? {
        return sharedPreference.getValueString(keyName)
    }

}