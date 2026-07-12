package com.example.messageapp.core

object TokenStorage {
    @Volatile
    private var token: String = ""

    fun setToken(value: String) {
        token = value
    }

    fun getToken(): String = token

    fun clear() {
        token = ""
    }
}
