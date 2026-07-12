package com.example.messageapp.domain.repoImpl

import android.util.Log
import android.util.MalformedJsonException
import com.example.messageapp.data.network.api.service.ApiService
import com.example.messageapp.data.network.model.AcceptFriendRequest
import com.example.messageapp.data.network.model.FriendRequest
import com.example.messageapp.data.network.model.FriendResponse
import com.example.messageapp.data.network.model.CommentRequest
import com.example.messageapp.data.network.model.ImageUploadResponse
import com.example.messageapp.data.network.model.LikeRequest
import com.example.messageapp.data.network.model.LoginRequest
import com.example.messageapp.data.network.model.LoginResponse
import com.example.messageapp.data.network.model.MessageResponse
import com.example.messageapp.data.network.model.NewsRequest
import com.example.messageapp.data.network.model.NewsResponse
import com.example.messageapp.data.network.model.UpdateProfileRequest
import com.example.messageapp.data.network.model.User
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.domain.repo.apiRepository.ApiRepository
import com.google.gson.Gson
import com.google.gson.JsonParseException
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class ApiServiceImpl @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson,
) : ApiRepository {

    override suspend fun addUser(user: User): Result<LoginResponse> {
        return safeApiCall { apiService.addUser(user) }
    }

    override suspend fun getCurrentUser(): Result<User> {
        return safeApiCall { apiService.getCurrentUser() }
    }

    override suspend fun findUserByName(username: UserRequest): Result<UserResponse> {
        return safeApiCall { apiService.findUserByName(username) }
    }

    override suspend fun allUser(): Result<List<UserResponse>> {
        return safeApiCall { apiService.allUser() }
    }

    override suspend fun findUserByStr(userName: UserRequest): Result<List<UserResponse>> {
        return safeApiCall { apiService.findUserByStr(userName) }
    }

    override suspend fun loginUser(loginRequest: LoginRequest): Result<LoginResponse> {
        return safeApiCall { apiService.loginUser(loginRequest) }
    }

    override suspend fun uploadNews(
        part: MultipartBody.Part,
        newsRequest: RequestBody
    ) {
        safeApiCall { apiService.uploadNews(part, newsRequest) }
    }

    override suspend fun allNews(): List<NewsResponse> = apiService.allNews()

    override suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
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

    override suspend fun uploadNewsWithOutImage(newsRequest: NewsRequest) {
        safeApiCall { apiService.uploadNewsWithOutImage(newsRequest) }
    }

    override suspend fun toggleLike(request: LikeRequest): Result<NewsResponse> {
        return safeApiCall { apiService.toggleLike(request) }
    }

    override suspend fun addComment(request: CommentRequest): Result<NewsResponse> {
        return safeApiCall { apiService.addComment(request) }
    }

    override suspend fun sendFriendRequest(friendRequest: FriendRequest): Result<FriendResponse> {
        return safeApiCall { apiService.sendFriendRequest(friendRequest) }
    }

    override suspend fun acceptFriend(request: AcceptFriendRequest): Result<FriendResponse> {
        return safeApiCall { apiService.acceptFriend(request) }
    }

    override suspend fun rejectFriend(request: AcceptFriendRequest): Result<FriendResponse> {
        return safeApiCall { apiService.rejectFriend(request) }
    }

    override suspend fun getFriends(username: String): Result<FriendResponse> {
        return safeApiCall { apiService.getFriends(username) }
    }

    override suspend fun getFriendRequests(username: String): Result<FriendResponse> {
        return safeApiCall { apiService.getFriendRequests(username) }
    }

    override suspend fun getMessages(user1: String, user2: String): Result<List<MessageResponse>> {
        return safeApiCall { apiService.getMessages(user1, user2) }
    }

    override suspend fun uploadMessageImage(part: MultipartBody.Part): Result<ImageUploadResponse> {
        return safeApiCall { apiService.uploadMessageImage(part) }
    }

    override suspend fun updateProfile(request: UpdateProfileRequest): Result<User> {
        return safeApiCall { apiService.updateProfile(request) }
    }

}
