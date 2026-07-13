package com.example.messageapp.domain.model

data class Chat(
    val id: String,
    val participants: List<String>,
    val lastMessage: Message? = null
)
