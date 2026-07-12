package com.example.messageapp.domain.repo.apiRepository

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
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface ApiRepository {

    suspend fun addUser(
        user: User
    ): Result<LoginResponse>

    suspend fun getCurrentUser(): Result<User>

    suspend fun findUserByName(
        username: UserRequest
    ): Result<UserResponse>

    suspend fun allUser(): Result<List<UserResponse>>

    suspend fun findUserByStr(userName: UserRequest): Result<List<UserResponse>>

    suspend fun loginUser(loginRequest: LoginRequest): Result<LoginResponse>

    suspend fun uploadNews(
        part: MultipartBody.Part,
        newsRequest: RequestBody
    )

    suspend fun allNews(): List<NewsResponse>

    suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T>

    suspend fun uploadNewsWithOutImage(newsRequest: NewsRequest)

    suspend fun toggleLike(request: LikeRequest): Result<NewsResponse>

    suspend fun addComment(request: CommentRequest): Result<NewsResponse>

    suspend fun sendFriendRequest(friendRequest: FriendRequest): Result<FriendResponse>

    suspend fun acceptFriend(request: AcceptFriendRequest): Result<FriendResponse>

    suspend fun rejectFriend(request: AcceptFriendRequest): Result<FriendResponse>

    suspend fun getFriends(username: String): Result<FriendResponse>

    suspend fun getFriendRequests(username: String): Result<FriendResponse>

    suspend fun getMessages(user1: String, user2: String): Result<List<MessageResponse>>

    suspend fun uploadMessageImage(part: MultipartBody.Part): Result<ImageUploadResponse>

    suspend fun updateProfile(request: UpdateProfileRequest): Result<User>
}
