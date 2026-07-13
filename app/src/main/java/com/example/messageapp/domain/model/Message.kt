package com.example.messageapp.domain.model

data class Message(
    val id: Int? = null,
    val senderUsername: String = "",
    val recipientUsername: String = "",
    val text: String,
    val isFromMe: Boolean,
    val type: String = "text"
)
