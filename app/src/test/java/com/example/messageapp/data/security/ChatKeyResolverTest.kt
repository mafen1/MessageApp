package com.example.messageapp.data.security

import com.example.messageapp.domain.repository.SecurityRepository
import com.example.messageapp.domain.security.AesEngine
import com.example.messageapp.domain.security.Base64Codec
import com.example.messageapp.domain.security.ChatKeyStorage
import com.example.messageapp.domain.security.LocalKeyStore
import com.example.messageapp.domain.security.WrappedKeyCopy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.util.Base64

class ChatKeyResolverTest {

    private val base64Codec = JvmBase64Codec()
    private lateinit var myStorage: FakeChatKeyStorage
    private lateinit var myKeyStore: FakeLocalKeyStore
    private lateinit var myManager: EncryptionManagerImpl
    private lateinit var peerManager: EncryptionManagerImpl
    private lateinit var peerStorage: FakeChatKeyStorage
    private lateinit var securityRepository: FakeSecurityRepository
    private lateinit var resolver: ChatKeyResolver
    private lateinit var peerResolver: ChatKeyResolver

    private val chatId = "@test1__@test13"
    private val me = "@test1"
    private val peer = "@test13"

    @Before
    fun setUp() {
        myStorage = FakeChatKeyStorage()
        myKeyStore = FakeLocalKeyStore()
        myManager = EncryptionManagerImpl(
            localKeyStore = myKeyStore,
            chatKeyStorage = myStorage,
            aesEngine = AesEngineImpl(base64Codec),
            rsaEngine = RsaEngineImpl()
        )
        // отдельное «устройство» пира со своим keystore и хранилищем
        peerStorage = FakeChatKeyStorage()
        peerManager = EncryptionManagerImpl(
            localKeyStore = FakeLocalKeyStore(),
            chatKeyStorage = peerStorage,
            aesEngine = AesEngineImpl(base64Codec),
            rsaEngine = RsaEngineImpl()
        )
        securityRepository = FakeSecurityRepository()
        securityRepository.publicKeys[me] = myManager.getLocalPublicKey()
        securityRepository.publicKeys[peer] = peerManager.getLocalPublicKey()

        resolver = ChatKeyResolver(
            encryptionManager = myManager,
            securityRepository = securityRepository,
            base64Codec = base64Codec,
            chatKeyStorage = myStorage
        )
        peerResolver = ChatKeyResolver(
            encryptionManager = peerManager,
            securityRepository = securityRepository,
            base64Codec = base64Codec,
            chatKeyStorage = peerStorage
        )
    }

    @Test
    fun `ensure fetches and unwraps server copy when local key is missing`() = runTest {
        val original = peerManager.getOrCreateChatKey(chatId)
        publishFrom(peerManager, mapOf(me to myKeyStore.getPublicKey(), peer to peerManager.getLocalPublicKey()))

        val result = resolver.ensure(chatId, peer, me)

        assertTrue(result)
        assertArrayEquals(original, myStorage.getChatKey(chatId))
    }

    @Test
    fun `ensure rotates and republishes when stored wrap cannot be unwrapped`() = runTest {
        val lostKey = peerManager.getOrCreateChatKey(chatId)
        // имитация потери keystore: на сервере обёртка чужим публичным ключом, локально ничего нет
        securityRepository.wrapped[key(chatId, me)] =
            WrappedKeyCopy(base64Codec.encode(peerManager.wrapChatKey(chatId, FakeLocalKeyStore().getPublicKey())), 1)

        val result = resolver.ensure(chatId, peer, me)

        assertTrue(result)
        val rotated = myStorage.getChatKey(chatId)!!
        assertNotEquals(lostKey.toList(), rotated.toList())

        // обе копии перезаписаны новым ключом и согласованы
        val rotatedForPeer = unwrapStored(peerManager, key(chatId, peer))
        val rotatedForMe = unwrapStored(myManager, key(chatId, me))
        assertArrayEquals(rotated, rotatedForPeer)
        assertArrayEquals(rotated, rotatedForMe)
        assertEquals(1, securityRepository.publishCalls)
    }

    @Test
    fun `ensure generates and publishes key when nothing exists anywhere`() = runTest {
        val result = resolver.ensure(chatId, peer, me)

        assertTrue(result)
        assertTrue(securityRepository.wrapped.containsKey(key(chatId, peer)))
        assertTrue(securityRepository.wrapped.containsKey(key(chatId, me)))
        assertNotNull(myStorage.getChatKey(chatId))
    }

    @Test
    fun `ensure adopts diverged server copy instead of keeping local key`() = runTest {
        val stale = myManager.getOrCreateChatKey(chatId)
        val fresh = peerManager.getOrCreateChatKey(chatId)
        assertNotEquals(stale.toList(), fresh.toList())
        // пир опубликовал свою эпоху обеим сторонам
        publishFrom(peerManager, mapOf(me to myKeyStore.getPublicKey(), peer to peerManager.getLocalPublicKey()))

        val result = resolver.ensure(chatId, peer, me)

        assertTrue(result)
        assertArrayEquals(fresh, myStorage.getChatKey(chatId))
    }

    @Test
    fun `second party adopts existing copy instead of publishing a rival key`() = runTest {
        assertTrue(resolver.ensure(chatId, peer, me))
        val myVersion = securityRepository.versionCounter

        // пир подключается позже: серверная копия уже есть -> принимается, а не перезаписывается
        assertTrue(peerResolver.ensure(chatId, me, peer))

        assertEquals(myVersion, securityRepository.versionCounter)
        assertArrayEquals(myStorage.getChatKey(chatId), peerStorage.getChatKey(chatId))
    }

    @Test
    fun `reinstall of peer rotates and heals transparently on my side`() = runTest {
        // обычный обмен: у обеих сторон общий ключ
        assertTrue(resolver.ensure(chatId, peer, me))
        assertTrue(peerResolver.ensure(chatId, me, peer))
        assertArrayEquals(myStorage.getChatKey(chatId), peerStorage.getChatKey(chatId))

        // «переустановка» приложения у пира: новый keystore, пустое хранилище, новый публичный ключ
        val reinstalledKeystore = FakeLocalKeyStore()
        val reinstalledStorage = FakeChatKeyStorage()
        securityRepository.publicKeys[peer] = reinstalledKeystore.getPublicKey()
        val reinstalledManager = EncryptionManagerImpl(
            localKeyStore = reinstalledKeystore,
            chatKeyStorage = reinstalledStorage,
            aesEngine = AesEngineImpl(base64Codec),
            rsaEngine = RsaEngineImpl()
        )
        val reinstalledResolver = ChatKeyResolver(
            encryptionManager = reinstalledManager,
            securityRepository = securityRepository,
            base64Codec = base64Codec,
            chatKeyStorage = reinstalledStorage
        )

        // у пира старая обёртка не открывается -> ротация на новую эпоху
        assertTrue(reinstalledResolver.ensure(chatId, me, peer))
        val rotated = reinstalledStorage.getChatKey(chatId)!!
        assertNotEquals(peerStorage.getChatKey(chatId)!!.toList(), rotated.toList())

        // я ничего не знаю о ротации, но следующий ensure синхронизируется с новой эпохой
        assertTrue(resolver.ensure(chatId, peer, me))

        assertArrayEquals(rotated, myStorage.getChatKey(chatId))
        assertArrayEquals(reinstalledStorage.getChatKey(chatId), myStorage.getChatKey(chatId))
    }

    @Test
    fun `previously diverged locals converge on latest server copy`() = runTest {
        // наследие старых версий приложения: у обоих свои локальные ключи, сервер пуст
        val myStale = myManager.getOrCreateChatKey(chatId)
        val peerStale = peerManager.getOrCreateChatKey(chatId)
        assertNotEquals(myStale.toList(), peerStale.toList())

        // новая публикация перекрывает оба локальных ключа
        val thirdDeviceStorage = FakeChatKeyStorage()
        val thirdDeviceManager = EncryptionManagerImpl(
            localKeyStore = FakeLocalKeyStore(),
            chatKeyStorage = thirdDeviceStorage,
            aesEngine = AesEngineImpl(base64Codec),
            rsaEngine = RsaEngineImpl()
        )
        val fresh = thirdDeviceManager.getOrCreateChatKey(chatId)
        publishFrom(thirdDeviceManager, mapOf(me to myKeyStore.getPublicKey(), peer to peerManager.getLocalPublicKey()))

        assertTrue(resolver.ensure(chatId, peer, me))
        assertTrue(peerResolver.ensure(chatId, me, peer))

        assertArrayEquals(fresh, myStorage.getChatKey(chatId))
        assertArrayEquals(fresh, peerStorage.getChatKey(chatId))
        assertEquals(1, securityRepository.publishCalls)
    }

    @Test
    fun `legacy unversioned copy triggers rotation and both sides converge`() = runTest {
        // наследие старого сервера: обёртки без эпохи (version=0), возможно разошедшиеся
        val myLegacy = myManager.getOrCreateChatKey(chatId)
        val peerLegacy = peerManager.getOrCreateChatKey(chatId)
        securityRepository.wrapped[key(chatId, me)] =
            WrappedKeyCopy(base64Codec.encode(myManager.wrapChatKey(chatId, myKeyStore.getPublicKey())), 0)
        securityRepository.wrapped[key(chatId, peer)] =
            WrappedKeyCopy(base64Codec.encode(peerManager.wrapChatKey(chatId, peerManager.getLocalPublicKey())), 0)

        // я отправляю сообщение: своя копия «синхронна», но версия 0 -> консервативная ротация
        assertTrue(resolver.ensure(chatId, peer, me))
        val rotated = myStorage.getChatKey(chatId)!!
        assertNotEquals(myLegacy.toList(), rotated.toList())
        assertEquals(1, securityRepository.versionCounter)

        // пир принимает новую эпоху при своей следующей отправке
        assertTrue(peerResolver.ensure(chatId, me, peer))

        assertArrayEquals(rotated, peerStorage.getChatKey(chatId))
        assertNotEquals(peerLegacy.toList(), peerStorage.getChatKey(chatId)!!.toList())
    }

    @Test
    fun `forceRefresh replaces stale local key with server copy`() = runTest {
        val stale = myManager.getOrCreateChatKey(chatId)
        val fresh = peerManager.getOrCreateChatKey(chatId)
        assertNotEquals(stale.toList(), fresh.toList())
        publishFrom(peerManager, mapOf(me to myKeyStore.getPublicKey(), peer to peerManager.getLocalPublicKey()))

        val result = resolver.ensure(chatId, peer, me, forceRefresh = true)

        assertTrue(result)
        assertArrayEquals(fresh, myStorage.getChatKey(chatId))
    }

    @Test
    fun `publish failure returns false so caller can retry`() = runTest {
        securityRepository.failUploads = true

        val result = resolver.ensure(chatId, peer, me)

        assertFalse(result)
    }

    @Test
    fun `placeholder constant matches encrypted prefix convention`() {
        assertTrue(AesEngine.UNDECRYPTABLE_PLACEHOLDER.isNotEmpty())
        assertFalse(AesEngine.UNDECRYPTABLE_PLACEHOLDER.startsWith(AesEngine.ENCRYPTED_PREFIX))
    }

    private suspend fun publishFrom(manager: EncryptionManagerImpl, recipients: Map<String, PublicKey>) {
        securityRepository.publishWrappedChatKeys(
            chatId,
            recipients.mapValues { (_, publicKey) -> base64Codec.encode(manager.wrapChatKey(chatId, publicKey)) }
        ).getOrThrow()
    }

    private fun unwrapStored(manager: EncryptionManagerImpl, storageKey: String): ByteArray =
        manager.unwrapChatKey(chatId, base64Codec.decode(securityRepository.wrapped[storageKey]!!.wrappedKey))

    private fun assertNotNull(any: Any?) = assertTrue(any != null)

    private class FakeSecurityRepository : SecurityRepository {
        val publicKeys = mutableMapOf<String, PublicKey>()
        val wrapped = mutableMapOf<String, WrappedKeyCopy>()
        var getWrappedCalls = 0
        var publishCalls = 0
        var versionCounter = 0L
        var failUploads = false

        override suspend fun uploadLocalPublicKey(): Result<Unit> = Result.success(Unit)

        override suspend fun getPublicKey(username: String): Result<PublicKey> {
            val key = publicKeys[username]
            return if (key != null) Result.success(key) else Result.failure(IllegalStateException("no key for $username"))
        }

        override suspend fun publishWrappedChatKeys(chatId: String, entries: Map<String, String>): Result<Long> {
            if (failUploads) return Result.failure(IllegalStateException("upload failed"))
            publishCalls++
            versionCounter++
            entries.forEach { (recipientUsername, wrappedKey) ->
                wrapped[key(chatId, recipientUsername)] = WrappedKeyCopy(wrappedKey, versionCounter)
            }
            return Result.success(versionCounter)
        }

        override suspend fun getWrappedChatKey(chatId: String, recipientUsername: String): Result<WrappedKeyCopy> {
            getWrappedCalls++
            val copy = wrapped[key(chatId, recipientUsername)]
            return if (copy != null) Result.success(copy) else Result.failure(IllegalStateException("not found"))
        }
    }

    private class FakeChatKeyStorage : ChatKeyStorage {
        private val keys = mutableMapOf<String, ByteArray>()

        override fun getChatKey(chatId: String): ByteArray? = keys[chatId]

        override fun saveChatKey(chatId: String, key: ByteArray) {
            keys[chatId] = key.copyOf()
        }

        override fun deleteChatKey(chatId: String) {
            keys.remove(chatId)
        }

        override fun clear() {
            keys.clear()
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

private fun key(chatId: String, recipient: String) = "$chatId|$recipient"
