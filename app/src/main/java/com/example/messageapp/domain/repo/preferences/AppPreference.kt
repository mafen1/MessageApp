package com.example.messageapp.domain.repo.preferences

interface AppPreference {
    fun save(keyName: String, value: String)
    fun getValueString(keyName: String): String?
}