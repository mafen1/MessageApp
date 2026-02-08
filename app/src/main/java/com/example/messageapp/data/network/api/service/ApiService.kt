package com.example.messageapp.data.network.api.service

import com.example.messageapp.data.network.model.LoginRequest
import com.example.messageapp.data.network.model.LoginResponse
import com.example.messageapp.data.network.model.NewsRequest
import com.example.messageapp.data.network.model.NewsResponse
import com.example.messageapp.data.network.model.NewsUploadWithOutImage
import com.example.messageapp.data.network.model.Token
import com.example.messageapp.data.network.model.User
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.data.network.model.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @POST("/register")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun addUser(@Body user: User): LoginResponse


    @POST("/getUserToken")
    suspend fun fetchUser(
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
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun loginUser(@Body loginRequest: LoginRequest): User

    @Multipart
    @POST("/uploadNews")
    suspend fun uploadNews(
        @Part
        part: MultipartBody.Part,
        @Part("NewsRequest")
        newsRequest: RequestBody
    )

    // todo News RESPONSE
    @GET("/allNews")
    suspend fun allNews(): List<NewsResponse>

    @POST("/uploadNewsWithOutImage")
    suspend fun uploadNewsWithOutImage(
        @Body newsUploadWithOutImage: NewsUploadWithOutImage
    )

}