package com.example.messageapp.domain.repository

import com.example.messageapp.domain.security.WrappedKeyCopy
import java.security.PublicKey

interface SecurityRepository {
    suspend fun uploadLocalPublicKey(): Result<Unit>
    suspend fun getPublicKey(username: String): Result<PublicKey>

    /**
     * Атомарно публикует обёртки чат-ключа для всех участников чата
     * (username -> wrapped base64). Возвращает назначенную сервером эпоху.
     */
    suspend fun publishWrappedChatKeys(chatId: String, entries: Map<String, String>): Result<Long>
    suspend fun getWrappedChatKey(chatId: String, recipientUsername: String): Result<WrappedKeyCopy>
}
