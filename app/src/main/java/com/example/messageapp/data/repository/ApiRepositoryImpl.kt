package com.example.messageapp.data.repository

import android.util.Log
import android.util.MalformedJsonException
import com.example.messageapp.data.mapper.toDomain
import com.example.messageapp.data.mapper.toDto
import com.example.messageapp.data.mapper.toLoginRequest
import com.example.messageapp.data.mapper.toRegisterDto
import com.example.messageapp.data.network.api.service.ApiService
import com.example.messageapp.data.network.model.AcceptFriendRequest
import com.example.messageapp.data.network.model.CommentRequest
import com.example.messageapp.data.network.model.FriendRequest as FriendRequestDto
import com.example.messageapp.data.network.model.LikeRequest
import com.example.messageapp.data.network.model.NewsRequest
import com.example.messageapp.data.network.model.UpdateProfileRequest
import com.example.messageapp.data.network.model.UserRequest
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
import com.google.gson.Gson
import com.google.gson.JsonParseException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson
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

    override suspend fun getMessages(user1: String, user2: String): Result<List<Message>> = safeApiCall {
        apiService.getMessages(user1, user2).map { it.toDomain(user1) }
    }

    override suspend fun uploadImage(imageBytes: ByteArray): Result<String> = safeApiCall {
        val part = MultipartBody.Part.createFormData(
            "file",
            "chat_${System.currentTimeMillis()}.jpg",
            imageBytes.toRequestBody("image/*".toMediaTypeOrNull(), 0, imageBytes.size)
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
            imageBytes.toRequestBody("image/*".toMediaTypeOrNull(), 0, imageBytes.size)
        )
        apiService.uploadNews(part, requestBody)
    }

    override suspend fun toggleLike(newsId: Int, userName: String): Result<NewsPost> = safeApiCall {
        apiService.toggleLike(LikeRequest(newsId, userName)).toDomain()
    }

    override suspend fun addComment(newsId: Int, userName: String, text: String): Result<NewsPost> = safeApiCall {
        apiService.addComment(CommentRequest(newsId, userName, text)).toDomain()
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return try {
            val result = apiCall()
            Log.d("API_SUCCESS", "Response: ${gson.toJson(result)}")
            Result.success(result)
        } catch (e: Exception) {
            if (e is JsonParseException || e is MalformedJsonException) {
                Log.e("JSON_ERROR", "Malformed JSON received. Check network logs for raw response", e)
            }
            Log.e("API_ERROR", "API call failed: ${e.message}", e)
            Result.failure(e)
        }
    }
}
