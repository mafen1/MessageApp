package com.example.messageapp.data.security

import com.example.messageapp.domain.security.AesEngine
import com.example.messageapp.domain.security.ChatKeyStorage
import com.example.messageapp.domain.security.EncryptionManager
import com.example.messageapp.domain.security.LocalKeyStore
import com.example.messageapp.domain.security.RsaEngine
import java.security.PublicKey
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EncryptionManagerImpl @Inject constructor(
    private val localKeyStore: LocalKeyStore,
    private val chatKeyStorage: ChatKeyStorage,
    private val aesEngine: AesEngine,
    private val rsaEngine: RsaEngine
) : EncryptionManager {

    override fun encrypt(chatId: String, plaintext: String): String {
        val key = requireChatKey(chatId)
        return aesEngine.encrypt(key, plaintext)
    }

    override fun decrypt(chatId: String, ciphertext: String): String {
        val key = chatKeyStorage.getChatKey(chatId)
        return if (key == null) {
            if (ciphertext.startsWith(AesEngine.ENCRYPTED_PREFIX)) {
                throw IllegalStateException("Missing chat key for $chatId")
            } else {
                ciphertext
            }
        } else {
            aesEngine.decrypt(key, ciphertext)
        }
    }

    override fun getOrCreateChatKey(chatId: String): ByteArray {
        chatKeyStorage.getChatKey(chatId)?.let { return it }

        val key = ByteArray(KEY_SIZE).apply { SecureRandom().nextBytes(this) }
        chatKeyStorage.saveChatKey(chatId, key)
        return key
    }

    override fun hasChatKey(chatId: String): Boolean = chatKeyStorage.getChatKey(chatId) != null

    override fun unwrapChatKey(chatId: String, wrappedKey: ByteArray): ByteArray {
        val keyPair = localKeyStore.getOrCreateKeyPair()
        val chatKey = rsaEngine.unwrapKey(wrappedKey, keyPair)
        chatKeyStorage.saveChatKey(chatId, chatKey)
        return chatKey
    }

    override fun wrapChatKey(chatId: String, recipientPublicKey: PublicKey): ByteArray {
        val chatKey = getOrCreateChatKey(chatId)
        return rsaEngine.wrapKey(chatKey, recipientPublicKey)
    }

    override fun getLocalPublicKey(): PublicKey = localKeyStore.getPublicKey()

    private fun requireChatKey(chatId: String): ByteArray {
        return chatKeyStorage.getChatKey(chatId)
            ?: throw IllegalStateException("No chat key for $chatId")
    }

    private companion object {
        private const val KEY_SIZE = 32
    }
}
