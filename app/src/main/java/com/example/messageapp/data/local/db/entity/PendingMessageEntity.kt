package com.example.messageapp.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_messages")
data class PendingMessageEntity(
    @PrimaryKey
    val clientMessageId: String,
    val chatId: String,
    val recipientUsername: String,
    val text: String,
    val type: String,
    val createdAt: Long,
    val retryCount: Int = 0
)
