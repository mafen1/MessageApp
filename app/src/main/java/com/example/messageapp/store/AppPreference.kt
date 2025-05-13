package com.example.messageapp.store

interface AppPreference {
    fun save(keyName: String, value: String)
    fun getValueString(keyName: String): String?
}