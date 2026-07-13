package com.example.messageapp.data.local.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.messageapp.domain.model.MessageStatus

@Entity(
    tableName = "messages",
    indices = [
        Index(value = ["chatId", "timestamp"]),
        Index(value = ["clientMessageId"], unique = true)
    ]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val clientMessageId: String,
    val chatId: String,
    val senderUsername: String,
    val recipientUsername: String,
    val text: String,
    val isFromMe: Boolean,
    val type: String,
    val status: MessageStatus,
    val timestamp: Long
)
