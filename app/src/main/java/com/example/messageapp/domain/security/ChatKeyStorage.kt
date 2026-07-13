package com.example.messageapp.domain.security

interface ChatKeyStorage {
    fun getChatKey(chatId: String): ByteArray?
    fun saveChatKey(chatId: String, key: ByteArray)
}
