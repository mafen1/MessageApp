package com.example.messageapp.data.network.api.service

import com.example.messageapp.data.network.model.LoginResponse
import com.example.messageapp.data.network.model.Token
import com.example.messageapp.data.network.model.User
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.data.network.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("/register")
    suspend fun addUser(
    @Body user: User
    ): Response<LoginResponse>
//    @GET("/friends")
//    suspend fun friends () : List<User>

    @POST("/findUserToken")
    suspend fun findUser(
       @Body token: Token
    ): User

    @POST("/findUserByName")
    suspend fun findUserByName(
        @Body username: UserRequest
    ): UserResponse
}