package com.example.messageapp.domain.model

data class User(
    val id: Int = 0,
    val name: String,
    val userName: String,
    val friends: List<String> = emptyList(),
    val token: String = ""
)
