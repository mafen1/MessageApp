package com.example.messageapp.data.repository

import android.util.Log
import android.util.MalformedJsonException
import com.example.messageapp.data.local.db.dao.MessageDao
import com.example.messageapp.data.mapper.toDomain
import com.example.messageapp.data.mapper.toDto
import com.example.messageapp.data.mapper.toEntity
import com.example.messageapp.data.mapper.toLoginRequest
import com.example.messageapp.data.mapper.toRegisterDto
import com.example.messageapp.data.network.api.service.ApiService
import com.example.messageapp.data.network.model.AcceptFriendRequest
import com.example.messageapp.data.network.model.CommentRequest
import com.example.messageapp.data.network.model.FriendRequest as FriendRequestDto
import com.example.messageapp.data.network.model.LikeRequest
import com.example.messageapp.data.network.model.UpdateProfileRequest
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.data.security.ChatKeyResolver
import com.example.messageapp.domain.model.FriendRequest
import com.example.messageapp.domain.model.LoggedInUser
import com.example.messageapp.domain.model.Message
import com.example.messageapp.domain.model.NewsPost
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.model.UserCredentials
import com.example.messageapp.domain.repository.AuthRepository
import com.example.messageapp.domain.repository.FriendRepository
import com.example.messageapp.domain.repository.MessageRepository
import com.example.messageapp.domain.repository.NewsRepository
import com.example.messageapp.domain.repository.UserRepository
import com.example.messageapp.domain.security.AesEngine
import com.example.messageapp.domain.security.EncryptionManager
import com.google.gson.Gson
import com.google.gson.JsonParseException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
    private val messageDao: MessageDao,
    private val encryptionManager: EncryptionManager,
    private val chatKeyResolver: ChatKeyResolver
) : AuthRepository, UserRepository, FriendRepository, MessageRepository, NewsRepository {

    override suspend fun register(credentials: UserCredentials): Result<LoggedInUser> = safeApiCall {
        apiService.addUser(credentials.toRegisterDto()).toDomain()
    }

    override suspend fun login(credentials: UserCredentials): Result<LoggedInUser> = safeApiCall {
        apiService.loginUser(credentials.toLoginRequest()).toDomain()
    }

    override suspend fun getCurrentUser(): Result<User> = safeApiCall {
        apiService.getCurrentUser().toDomain()
    }

    override suspend fun findUserByName(username: String): Result<User> = safeApiCall {
        apiService.findUserByName(UserRequest(username)).toDomain()
    }

    override suspend fun findUsersByString(query: String): Result<List<User>> = safeApiCall {
        apiService.findUserByStr(UserRequest(query)).map { it.toDomain() }
    }

    override suspend fun getAllUsers(): Result<List<User>> = safeApiCall {
        apiService.allUser().map { it.toDomain() }
    }

    override suspend fun updateProfile(
        userName: String,
        name: String,
        password: String?
    ): Result<User> = safeApiCall {
        apiService.updateProfile(UpdateProfileRequest(userName, name, password)).toDomain()
    }

    override suspend fun getFriends(username: String): Result<List<String>> = safeApiCall {
        apiService.getFriends(username).friends ?: emptyList()
    }

    override suspend fun getFriendRequests(username: String): Result<List<FriendRequest>> = safeApiCall {
        apiService.getFriendRequests(username).requests?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun sendFriendRequest(
        senderUsername: String,
        receiverUsername: String
    ): Result<String> = safeApiCall {
        apiService.sendFriendRequest(
            FriendRequestDto(
                senderUserName = senderUsername,
                receiverUserName = receiverUsername,
                status = "pending"
            )
        ).message
    }

    override suspend fun acceptFriendRequest(
        senderUsername: String,
        receiverUsername: String
    ): Result<String> = safeApiCall {
        apiService.acceptFriend(
            AcceptFriendRequest(senderUsername, receiverUsername)
        ).message
    }

    override suspend fun rejectFriendRequest(
        senderUsername: String,
        receiverUsername: String
    ): Result<String> = safeApiCall {
        apiService.rejectFriend(
            AcceptFriendRequest(senderUsername, receiverUsername)
        ).message
    }

    override suspend fun getMessages(user1: String, user2: String): Result<List<Message>> {
        val chatId = chatId(user1, user2)
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
            Log.e("MessageRepo", "Failed to fetch messages, returning local", e)
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
            Log.w("MessageRepo", "History key heal failed for $chatId", e)
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

    override suspend fun uploadImage(imageBytes: ByteArray): Result<String> = safeApiCall {
        val part = MultipartBody.Part.createFormData(
            "file",
            "chat_${System.currentTimeMillis()}.jpg",
            imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, imageBytes.size)
        )
        apiService.uploadMessageImage(part).fileName
    }

    override suspend fun getNewsFeed(): Result<List<NewsPost>> = safeApiCall {
        apiService.allNews().map { it.toDomain() }
    }

    override suspend fun createPost(post: NewsPost): Result<Unit> = safeApiCall {
        apiService.uploadNewsWithOutImage(post.toDto())
    }

    override suspend fun createPostWithImage(post: NewsPost, imageBytes: ByteArray): Result<Unit> = safeApiCall {
        val newsRequest = post.toDto()
        val requestBody = gson.toJson(newsRequest)
            .toRequestBody("application/json".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData(
            "file",
            "news_${System.currentTimeMillis()}.jpg",
            imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, imageBytes.size)
        )
        apiService.uploadNews(part, requestBody)
    }

    override suspend fun toggleLike(newsId: Int, userName: String): Result<NewsPost> = safeApiCall {
        apiService.toggleLike(LikeRequest(newsId, userName)).toDomain()
    }

    override suspend fun addComment(newsId: Int, userName: String, text: String): Result<NewsPost> = safeApiCall {
        apiService.addComment(CommentRequest(newsId, userName, text)).toDomain()
    }

    private fun chatId(user1: String, user2: String): String {
        return listOf(user1, user2).sorted().joinToString("__")
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return try {
            val result = apiCall()
            Log.d("API_SUCCESS", "Response: ${gson.toJson(result)}")
            Result.success(result)
        } catch (e: HttpException) {
            val errorBody = try { e.response()?.errorBody()?.string() } catch (_: Exception) { null }
            val serverMessage = try {
                if (!errorBody.isNullOrBlank()) {
                    val json = gson.fromJson(errorBody, com.google.gson.JsonObject::class.java)
                    when {
                        json.has("message") -> json.get("message").asString
                        json.has("error") -> json.get("error").asString
                        else -> errorBody
                    }
                } else null
            } catch (_: Exception) { errorBody }
            val message = serverMessage?.takeIf { it.isNotBlank() } ?: e.message ?: "HTTP ${e.code()}"
            Log.e("API_ERROR", "API call failed ${e.code()}: $message body=$errorBody", e)
            Result.failure(Exception(message, e))
        } catch (e: kotlinx.coroutines.CancellationException) {
            // не глотаем отмену корутины (фикс M11)
            throw e
        } catch (e: Exception) {
            if (e is JsonParseException || e is MalformedJsonException) {
                Log.e("JSON_ERROR", "Malformed JSON received. Check network logs for raw response", e)
            }
            Log.e("API_ERROR", "API call failed: ${e.message}", e)
            Result.failure(e)
        }
    }
}
