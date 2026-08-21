package com.example.messageapp.data.security

import android.util.Log
import com.example.messageapp.domain.repository.SecurityRepository
import com.example.messageapp.domain.security.Base64Codec
import com.example.messageapp.domain.security.ChatKeyStorage
import com.example.messageapp.domain.security.EncryptionManager
import kotlinx.coroutines.CancellationException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Единая точка получения чат-ключа. Серверная копия авторитетна:
 * 1. локальный ключ есть -> сверяем с серверной копией (unwrap + сравнение байт):
 *    совпадает - ок; отличается - принимаем серверную (чужая ротация не страшна);
 *    unwrap невозможен - РОТАЦИЯ на новую эпоху;
 * 2. локального нет - принимаем серверную копию либо генерируем новую;
 * 3. публикация всегда атомарна: обе обёртки (пиру и себе) одним запросом,
 *    сервер инкрементит эпоху чата - гонки одновременных публикаций сходятся.
 */
@Singleton
class ChatKeyResolver @Inject constructor(
    private val encryptionManager: EncryptionManager,
    private val securityRepository: SecurityRepository,
    private val base64Codec: Base64Codec,
    private val chatKeyStorage: ChatKeyStorage
) {

    suspend fun ensure(
        chatId: String,
        peerUsername: String,
        meUsername: String,
        forceRefresh: Boolean = false
    ): Boolean {
        val remote = securityRepository.getWrappedChatKey(chatId, meUsername).getOrNull()
        val local = chatKeyStorage.getChatKey(chatId)

        if (!forceRefresh && local != null) {
            if (remote == null) {
                Log.i(TAG, "Server copy missing for $chatId — publishing local key")
                return publishCurrentKey(chatId, peerUsername, meUsername)
            }

            val unwrapped = tryUnwrap(remote.wrappedKey)
            if (unwrapped == null) {
                // локальный keystore не может открыть серверную копию (потеря/смена keystore) — ротация
                Log.w(TAG, "Unwrap of stored key failed for $chatId, rotating")
                return rotateAndPublish(chatId, peerUsername, meUsername)
            }

            if (remote.version <= 0L) {
                // легаси-строка без эпохи (до внедрения версионирования): её соответствие
                // копии пира не гарантировано — консервативно ротируем на свежую эпоху
                Log.i(TAG, "Legacy unversioned key copy for $chatId — rotating to a fresh epoch")
                return rotateAndPublish(chatId, peerUsername, meUsername)
            }

            return if (unwrapped.contentEquals(local)) {
                true
            } else {
                // кто-то опубликовал новую эпоху — принимаем её вместо локальной,
                // чтобы отправляемые шифротексты совпадали с ключом пира
                Log.i(TAG, "Adopting server copy for $chatId (epoch=${remote.version})")
                chatKeyStorage.saveChatKey(chatId, unwrapped)
                true
            }
        }

        // локального ключа нет либо форс-обновление: серверная копия приоритетнее
        if (remote != null) {
            val unwrapped = tryUnwrap(remote.wrappedKey)
            if (unwrapped != null) {
                chatKeyStorage.saveChatKey(chatId, unwrapped)
                return true
            }
            Log.w(TAG, "Unwrap of stored key failed for $chatId, rotating")
        }

        if (local != null && remote == null) {
            // пустой сервер при форс-обновлении не повод уничтожать валидный локальный ключ
            return publishCurrentKey(chatId, peerUsername, meUsername)
        }

        return rotateAndPublish(chatId, peerUsername, meUsername)
    }

    private suspend fun rotateAndPublish(chatId: String, peer: String, me: String): Boolean {
        // сбрасываем битый/устаревший локальный ключ, чтобы сгенерировался новый
        chatKeyStorage.deleteChatKey(chatId)
        encryptionManager.getOrCreateChatKey(chatId)
        return publishCurrentKey(chatId, peer, me)
    }

    private suspend fun publishCurrentKey(chatId: String, peer: String, me: String): Boolean {
        val entries = LinkedHashMap<String, String>()
        for (recipient in linkedSetOf(peer, me)) {
            try {
                val publicKey = securityRepository.getPublicKey(recipient).getOrThrow()
                val wrapped = encryptionManager.wrapChatKey(chatId, publicKey)
                entries[recipient] = base64Codec.encode(wrapped)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.w(TAG, "Failed to wrap key for $recipient", e)
                return false
            }
        }

        return try {
            val version = securityRepository.publishWrappedChatKeys(chatId, entries).getOrThrow()
            Log.i(TAG, "Chat key published for $chatId (epoch=$version, peer=$peer me=$me)")
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Publishing key for $chatId incomplete", e)
            false
        }
    }

    private fun tryUnwrap(wrappedBase64: String): ByteArray? {
        return try {
            encryptionManager.tryUnwrapChatKey(base64Codec.decode(wrappedBase64))
        } catch (_: Exception) {
            null
        }
    }

    private companion object {
        private const val TAG = "ChatKeyResolver"
    }
}
