package com.example.messageapp.domain.repository

import com.example.messageapp.domain.model.Message

interface MessageRepository {
    suspend fun getMessages(user1: String, user2: String): Result<List<Message>>
    suspend fun uploadImage(imageBytes: ByteArray): Result<String>
}
