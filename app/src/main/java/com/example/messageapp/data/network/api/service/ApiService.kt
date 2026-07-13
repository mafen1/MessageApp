package com.example.messageapp.data.network.api.service

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
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("/register")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun addUser(@Body user: User): LoginResponse

    @GET("/me")
    suspend fun getCurrentUser(): User

    @POST("/findUserByName")
    suspend fun findUserByName(
        @Body username: UserRequest
    ): UserResponse

    @GET("/allUser")
    suspend fun allUser(): List<UserResponse>

    @POST("/findUserByStr")
    suspend fun findUserByStr(@Body userName: UserRequest): List<UserResponse>

    @POST("/login")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun loginUser(@Body loginRequest: LoginRequest): LoginResponse

    @Multipart
    @POST("/uploadNews")
    suspend fun uploadNews(
        @Part
        part: MultipartBody.Part,
        @Part("NewsRequest")
        newsRequest: RequestBody
    )

    @GET("/allNews")
    suspend fun allNews(): List<NewsResponse>

    @POST("/uploadNewsWithOutImage")
    suspend fun uploadNewsWithOutImage(
        @Body newsRequest: NewsRequest
    )

    @POST("/news/like")
    suspend fun toggleLike(@Body request: LikeRequest): NewsResponse

    @POST("/news/comment")
    suspend fun addComment(@Body request: CommentRequest): NewsResponse

    @POST("/requestFriend")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun sendFriendRequest(@Body friendRequest: FriendRequest): FriendResponse

    @POST("/acceptFriend")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun acceptFriend(@Body request: AcceptFriendRequest): FriendResponse

    @POST("/rejectFriend")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun rejectFriend(@Body request: AcceptFriendRequest): FriendResponse

    @GET("/friends/{username}")
    suspend fun getFriends(@Path("username") username: String): FriendResponse

    @GET("/friendRequests/{username}")
    suspend fun getFriendRequests(@Path("username") username: String): FriendResponse

    @GET("/messages/{user1}/{user2}")
    suspend fun getMessages(
        @Path("user1") user1: String,
        @Path("user2") user2: String
    ): List<MessageResponse>

    @Multipart
    @POST("/uploadMessageImage")
    suspend fun uploadMessageImage(
        @Part part: MultipartBody.Part
    ): ImageUploadResponse

    @POST("/keys")
    suspend fun uploadPublicKey(@Body request: com.example.messageapp.data.network.model.PublicKeyRequest)

    @GET("/keys/{username}")
    suspend fun getPublicKey(@Path("username") username: String): com.example.messageapp.data.network.model.PublicKeyResponse

    @POST("/chat-keys")
    suspend fun uploadWrappedChatKey(@Body request: com.example.messageapp.data.network.model.WrappedKeyRequest)

    @GET("/chat-keys/{chatId}")
    suspend fun getWrappedChatKey(
        @Path("chatId") chatId: String,
        @Query("recipient") recipient: String
    ): com.example.messageapp.data.network.model.WrappedKeyResponse

    @POST("/updateProfile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): User

}
