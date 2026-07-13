package com.example.messageapp.domain.model

data class LoggedInUser(
    val token: String,
    val expiresAt: String,
    val user: User
)
