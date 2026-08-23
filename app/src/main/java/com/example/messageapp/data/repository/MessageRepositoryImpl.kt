package com.example.messageapp.data.repository

import android.util.Log
import com.example.messageapp.core.buildChatId
import com.example.messageapp.data.local.db.dao.ChatDao
import com.example.messageapp.data.local.db.dao.MessageDao
import com.example.messageapp.data.local.db.dao.PendingMessageDao
import com.example.messageapp.data.mapper.toDomain
import com.example.messageapp.data.mapper.toEntity
import com.example.messageapp.data.network.api.service.ApiService
import com.example.messageapp.data.security.ChatKeyResolver
import com.example.messageapp.domain.model.Message
import com.example.messageapp.domain.repository.MessageRepository
import com.example.messageapp.domain.security.AesEngine
import com.example.messageapp.domain.security.EncryptionManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val messageDao: MessageDao,
    private val chatDao: ChatDao,
    private val pendingMessageDao: PendingMessageDao,
    private val encryptionManager: EncryptionManager,
    private val chatKeyResolver: ChatKeyResolver
) : MessageRepository {

    override suspend fun getMessages(user1: String, user2: String): Result<List<Message>> {
        val chatId = buildChatId(user1, user2)
        return try {
            val remoteMessages = apiService.getMessages(user1, user2).map { it.toDomain(user1) }
            val decrypted = decryptHistory(chatId, user1, user2, remoteMessages)
            // кэшируем только успешно расшифрованные, чтобы не консервировать плейсхолдеры
            val toCache = decrypted.filter { it.second }.map { it.first }
            if (toCache.isNotEmpty()) {
                messageDao.insertAll(toCache.map { it.toEntity(chatId) })
            } else if (decrypted.isNotEmpty() && messageDao.getMessages(chatId).isEmpty()) {
                // если локально пусто — кэшируем как есть, чтобы не терять историю
                messageDao.insertAll(decrypted.map { it.first }.map { it.toEntity(chatId) })
            }
            Result.success(decrypted.map { it.first })
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch messages, returning local", e)
            val local = messageDao.getMessages(chatId).map { it.toDomain() }.map { msg ->
                try {
                    msg.copy(text = encryptionManager.decrypt(chatId, msg.text))
                } catch (_: Exception) { msg }
            }
            if (local.isNotEmpty()) {
                Result.success(local)
            } else {
                Result.failure(e)
            }
        }
    }

    /**
     * Расшифровка истории: при неудаче — одна попытка самолечения ключа
     * (forceRefresh: серверная копия приоритетнее), затем плейсхолдер вместо сырого ENC:
     */
    private suspend fun decryptHistory(
        chatId: String,
        me: String,
        other: String,
        messages: List<Message>
    ): List<Pair<Message, Boolean>> {
        if (messages.isEmpty()) return emptyList()

        fun decryptOne(text: String): String? = try {
            encryptionManager.decrypt(chatId, text)
        } catch (_: Exception) {
            null
        }

        var entries = messages.map { m ->
            decryptOne(m.text)?.let { m.copy(text = it) to true }
                ?: (m.copy(text = AesEngine.UNDECRYPTABLE_PLACEHOLDER) to false)
        }

        if (entries.all { it.second }) return entries

        // самолечение только для зашифрованных неудачных сообщений
        val hasEncryptedFailures = entries.indices.any { i ->
            !entries[i].second && messages[i].text.startsWith(AesEngine.ENCRYPTED_PREFIX)
        }
        if (!hasEncryptedFailures) return entries

        val healed = try {
            chatKeyResolver.ensure(chatId, other, me, forceRefresh = true)
        } catch (e: Exception) {
            Log.w(TAG, "History key heal failed for $chatId", e)
            false
        }

        if (!healed) return entries

        entries = entries.mapIndexed { index, entry ->
            if (entry.second || !messages[index].text.startsWith(AesEngine.ENCRYPTED_PREFIX)) {
                entry
            } else {
                decryptOne(messages[index].text)?.let { entry.first.copy(text = it) to true } ?: entry
            }
        }
        return entries
    }

    override suspend fun saveMessage(message: Message, chatId: String) {
        messageDao.insert(message.toEntity(chatId))
    }

    override suspend fun clearAllLocalData() {
        pendingMessageDao.clearAll()
        chatDao.clearAll()
        messageDao.clearAll()
    }

    override suspend fun uploadImage(imageBytes: ByteArray): Result<String> = safeApiCall {
        val part = MultipartBody.Part.createFormData(
            "file",
            "chat_${System.currentTimeMillis()}.jpg",
            imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, imageBytes.size)
        )
        apiService.uploadMessageImage(part).fileName
    }

    private companion object {
        private const val TAG = "MessageRepo"
    }
}
