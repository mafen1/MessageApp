package com.example.messageapp.domain.useCase

import com.example.messageapp.data.network.model.LoginRequest
import com.example.messageapp.data.network.model.LoginResponse
import com.example.messageapp.data.network.model.NewsRequest
import com.example.messageapp.data.network.model.Token
import com.example.messageapp.data.network.model.User
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.domain.repoImpl.ApiServiceImpl
import okhttp3.MultipartBody
import retrofit2.Response
import javax.inject.Inject

class ApiServiceUseCase @Inject constructor(private val apiServiceImpl: ApiServiceImpl) {

    suspend fun addUser(user: User): Response<LoginResponse> {
        return apiServiceImpl.addUser(user)
    }

    suspend fun findUser(
        token: Token
    ): Result<User> {
        return apiServiceImpl.findUser(token)
    }

    suspend fun findUserByName(
        username: UserRequest
    ): Result<UserResponse> {
        return apiServiceImpl.findUserByName(username)
    }

    suspend fun allUser(): Result<MutableList<UserResponse>> {
        return apiServiceImpl.allUser()
    }

    suspend fun findUserByStr(userName: UserRequest): Result<List<UserResponse>> {
        return apiServiceImpl.findUserByStr(userName)
    }

    suspend fun loginUser(loginRequest: LoginRequest): Result<User> {
        return apiServiceImpl.loginUser(loginRequest)
    }

    suspend fun sendImage(part: MultipartBody.Part, newsRequest: NewsRequest){
        apiServiceImpl.sendImage(part, newsRequest)
    }

    suspend fun addNews(newsRequest: NewsRequest){
        apiServiceImpl.addNews(newsRequest)
    }

}