package com.example.messageapp.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chats")
data class ChatEntity(
    @PrimaryKey
    val id: String,
    val participantUserName: String,
    val participantName: String,
    val lastMessageText: String? = null,
    val lastMessageTime: Long? = null,
    val unreadCount: Int = 0
)
