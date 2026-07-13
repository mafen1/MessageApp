package com.example.messageapp.domain.repository

import java.security.PublicKey

interface SecurityRepository {
    suspend fun uploadLocalPublicKey(): Result<Unit>
    suspend fun getPublicKey(username: String): Result<PublicKey>
    suspend fun uploadWrappedChatKey(
        chatId: String,
        recipientUsername: String,
        wrappedKey: String
    ): Result<Unit>

    suspend fun getWrappedChatKey(chatId: String, recipientUsername: String): Result<String>
}
