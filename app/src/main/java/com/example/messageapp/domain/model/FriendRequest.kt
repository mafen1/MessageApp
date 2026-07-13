package com.example.messageapp.domain.model

data class FriendRequest(
    val id: Int = 0,
    val senderUserName: String,
    val receiverUserName: String,
    val status: String = "pending"
)
