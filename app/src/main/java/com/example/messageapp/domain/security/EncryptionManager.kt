package com.example.messageapp.domain.security

interface EncryptionManager {
    fun encrypt(chatId: String, plaintext: String): String
    fun decrypt(chatId: String, ciphertext: String): String
    fun getOrCreateChatKey(chatId: String): ByteArray
    fun hasChatKey(chatId: String): Boolean
    fun unwrapChatKey(chatId: String, wrappedKey: ByteArray): ByteArray
    fun wrapChatKey(chatId: String, recipientPublicKey: java.security.PublicKey): ByteArray
    fun getLocalPublicKey(): java.security.PublicKey
}
