package com.example.messageapp.data.security

import com.example.messageapp.domain.security.Base64Codec
import com.example.messageapp.domain.security.ChatKeyStorage
import com.example.messageapp.domain.security.LocalKeyStore
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.util.Base64

class EncryptionManagerImplTest {

    private val base64Codec = JvmBase64Codec()
    private lateinit var chatKeyStorage: FakeChatKeyStorage
    private lateinit var localKeyStore: FakeLocalKeyStore
    private lateinit var manager: EncryptionManagerImpl

    @Before
    fun setUp() {
        chatKeyStorage = FakeChatKeyStorage()
        localKeyStore = FakeLocalKeyStore()
        manager = EncryptionManagerImpl(
            localKeyStore = localKeyStore,
            chatKeyStorage = chatKeyStorage,
            aesEngine = AesEngineImpl(base64Codec),
            rsaEngine = RsaEngineImpl()
        )
    }

    @Test
    fun `getOrCreateChatKey returns same key for the same chat`() {
        val first = manager.getOrCreateChatKey("chat_1")
        val second = manager.getOrCreateChatKey("chat_1")

        assertNotNull(first)
        assertEquals(32, first.size)
        assertArrayEquals(first, second)
    }

    @Test
    fun `getOrCreateChatKey returns different keys for different chats`() {
        val keyA = manager.getOrCreateChatKey("chat_a")
        val keyB = manager.getOrCreateChatKey("chat_b")

        assertNotEquals(keyA.toList(), keyB.toList())
    }

    @Test
    fun `hasChatKey returns false before creation and true after`() {
        assertEquals(false, manager.hasChatKey("chat_x"))
        manager.getOrCreateChatKey("chat_x")
        assertEquals(true, manager.hasChatKey("chat_x"))
    }

    @Test
    fun `encrypt then decrypt returns original plaintext`() {
        manager.getOrCreateChatKey("chat_1")
        val plaintext = "Hello, E2E encryption!"

        val ciphertext = manager.encrypt("chat_1", plaintext)
        val decrypted = manager.decrypt("chat_1", ciphertext)

        assertNotEquals(plaintext, ciphertext)
        assertEquals(plaintext, decrypted)
    }

    @Test
    fun `decrypt returns plaintext when ciphertext is not encrypted`() {
        val legacy = "legacy plaintext message"

        val result = manager.decrypt("chat_1", legacy)

        assertEquals(legacy, result)
    }

    @Test
    fun `wrap and unwrap chat key restores key for recipient`() {
        val senderStore = FakeLocalKeyStore()
        val recipientStore = FakeLocalKeyStore()

        val sender = EncryptionManagerImpl(
            localKeyStore = senderStore,
            chatKeyStorage = FakeChatKeyStorage(),
            aesEngine = AesEngineImpl(base64Codec),
            rsaEngine = RsaEngineImpl()
        )
        val recipient = EncryptionManagerImpl(
            localKeyStore = recipientStore,
            chatKeyStorage = FakeChatKeyStorage(),
            aesEngine = AesEngineImpl(base64Codec),
            rsaEngine = RsaEngineImpl()
        )

        val chatKey = sender.getOrCreateChatKey("chat_1")
        val wrapped = sender.wrapChatKey("chat_1", recipient.getLocalPublicKey())
        val unwrapped = recipient.unwrapChatKey("chat_1", wrapped)

        assertArrayEquals(chatKey, unwrapped)
    }

    @Test
    fun `getLocalPublicKey returns stable public key`() {
        val first = manager.getLocalPublicKey()
        val second = manager.getLocalPublicKey()

        assertNotNull(first)
        assertEquals(first, second)
    }

    private class FakeChatKeyStorage : ChatKeyStorage {
        private val keys = mutableMapOf<String, ByteArray>()

        override fun getChatKey(chatId: String): ByteArray? = keys[chatId]

        override fun saveChatKey(chatId: String, key: ByteArray) {
            keys[chatId] = key.copyOf()
        }
    }

    private class FakeLocalKeyStore : LocalKeyStore {
        private val keyPair: KeyPair by lazy {
            KeyPairGenerator.getInstance("RSA").apply { initialize(2048) }.generateKeyPair()
        }

        override fun getOrCreateKeyPair(): KeyPair = keyPair

        override fun getPublicKey(): PublicKey = keyPair.public
    }

    private class JvmBase64Codec : Base64Codec {
        override fun encode(bytes: ByteArray): String = Base64.getEncoder().encodeToString(bytes)
        override fun decode(encoded: String): ByteArray = Base64.getDecoder().decode(encoded)
    }
}
