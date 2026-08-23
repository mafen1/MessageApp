package com.example.messageapp.domain.security

interface ChatKeyStorage {
    fun getChatKey(chatId: String): ByteArray?
    fun saveChatKey(chatId: String, key: ByteArray)
    fun deleteChatKey(chatId: String)

    /** Полная очистка (logout): чат-ключи принадлежат конкретному аккаунту. */
    fun clear()
}
