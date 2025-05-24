package com.example.messageapp.data.network.api.service

import com.example.messageapp.data.network.model.LoginRequest
import com.example.messageapp.data.network.model.LoginResponse
import com.example.messageapp.data.network.model.Token
import com.example.messageapp.data.network.model.User
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.data.network.model.UserResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @POST("/register")
    suspend fun addUser(
        @Body user: User
    ): Response<LoginResponse>


    @POST("/findUserToken")
    suspend fun findUser(
        @Body token: Token
    ): User

    @POST("/findUserByName")
    suspend fun findUserByName(
        @Body username: UserRequest
    ): UserResponse

    @GET("/allUser")
    suspend fun allUser(): MutableList<UserResponse>

    @POST("/findUserByStr")
    suspend fun findUserByStr(@Body userName: UserRequest): List<UserResponse>

    @POST("/login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): User

    @Multipart
    @POST("/uploadNews")
    suspend fun addNews(
        @Part
        part: MultipartBody.Part
    )
}