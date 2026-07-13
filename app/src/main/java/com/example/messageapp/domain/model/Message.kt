package com.example.messageapp.domain.model

data class Message(
    val id: Int? = null,
    val clientMessageId: String = "",
    val senderUsername: String = "",
    val recipientUsername: String = "",
    val text: String,
    val isFromMe: Boolean,
    val type: String = "text",
    val status: MessageStatus = MessageStatus.SENT,
    val timestamp: Long = System.currentTimeMillis()
)
