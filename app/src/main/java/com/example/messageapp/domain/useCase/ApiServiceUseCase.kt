package com.example.messageapp.domain.useCase

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
import com.example.messageapp.domain.repoImpl.ApiServiceImpl
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class ApiServiceUseCase @Inject constructor(private val apiServiceImpl: ApiServiceImpl) {

    suspend fun addUser(user: User): Result<LoginResponse> {
        return apiServiceImpl.addUser(user)
    }

    suspend fun getCurrentUser(): Result<User> {
        return apiServiceImpl.getCurrentUser()
    }

    suspend fun findUserByName(
        username: UserRequest
    ): Result<UserResponse> {
        return apiServiceImpl.findUserByName(username)
    }

    suspend fun allUser(): Result<List<UserResponse>> {
        return apiServiceImpl.allUser()
    }

    suspend fun findUserByStr(userName: UserRequest): Result<List<UserResponse>> {
        return apiServiceImpl.findUserByStr(userName)
    }

    suspend fun loginUser(loginRequest: LoginRequest): Result<LoginResponse> {
        return apiServiceImpl.loginUser(loginRequest)
    }

    suspend fun uploadNews(
        part: MultipartBody.Part,
        newsRequestBody: RequestBody
    ) {
        apiServiceImpl.uploadNews(part, newsRequestBody)
    }

    suspend fun allNews(): List<NewsResponse> {
        return apiServiceImpl.allNews()
    }

    suspend fun uploadNewsWithOutImage(newsRequest: NewsRequest) =
        apiServiceImpl.uploadNewsWithOutImage(newsRequest)

    suspend fun toggleLike(request: LikeRequest): Result<NewsResponse> =
        apiServiceImpl.toggleLike(request)

    suspend fun addComment(request: CommentRequest): Result<NewsResponse> =
        apiServiceImpl.addComment(request)

    suspend fun sendFriendRequest(friendRequest: FriendRequest): Result<FriendResponse> =
        apiServiceImpl.sendFriendRequest(friendRequest)

    suspend fun acceptFriend(request: AcceptFriendRequest): Result<FriendResponse> =
        apiServiceImpl.acceptFriend(request)

    suspend fun rejectFriend(request: AcceptFriendRequest): Result<FriendResponse> =
        apiServiceImpl.rejectFriend(request)

    suspend fun getFriends(username: String): Result<FriendResponse> =
        apiServiceImpl.getFriends(username)

    suspend fun getFriendRequests(username: String): Result<FriendResponse> =
        apiServiceImpl.getFriendRequests(username)

    suspend fun getMessages(user1: String, user2: String): Result<List<MessageResponse>> =
        apiServiceImpl.getMessages(user1, user2)

    suspend fun uploadMessageImage(part: MultipartBody.Part): Result<ImageUploadResponse> =
        apiServiceImpl.uploadMessageImage(part)

    suspend fun updateProfile(request: UpdateProfileRequest): Result<User> =
        apiServiceImpl.updateProfile(request)

}
