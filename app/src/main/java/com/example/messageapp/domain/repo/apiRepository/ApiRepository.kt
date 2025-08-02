package com.example.messageapp.domain.repo.apiRepository

import com.example.messageapp.data.network.model.LoginRequest
import com.example.messageapp.data.network.model.LoginResponse
import com.example.messageapp.data.network.model.NewsRequest
import com.example.messageapp.data.network.model.NewsResponse
import com.example.messageapp.data.network.model.Token
import com.example.messageapp.data.network.model.User
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.data.network.model.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Part

interface ApiRepository {

    suspend fun addUser(
        user: User
    ): Response<LoginResponse>

    suspend fun findUser(
        token: Token
    ): Result<User>

    suspend fun findUserByName(
        username: UserRequest
    ): Result<UserResponse>

    suspend fun allUser(): Result<MutableList<UserResponse>>

    suspend fun findUserByStr(userName: UserRequest): Result<List<UserResponse>>

    suspend fun loginUser(loginRequest: LoginRequest): Result<User>

//    suspend fun sendImage(image: MultipartBody.Part, newsRequest: NewsRequest)

    suspend fun addNews(newsRequest: NewsRequest)

    suspend fun uploadNews(
        part: MultipartBody.Part,
        nameNews: RequestBody,
        userName: RequestBody
    )

    suspend fun allNews(): List<NewsResponse>

}