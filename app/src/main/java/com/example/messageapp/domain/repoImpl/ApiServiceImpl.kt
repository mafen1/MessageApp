package com.example.messageapp.domain.repoImpl

import android.content.Context
import android.util.Log
import com.example.messageapp.data.network.api.service.ApiService
import com.example.messageapp.data.network.model.LoginRequest
import com.example.messageapp.data.network.model.LoginResponse
import com.example.messageapp.data.network.model.NewsRequest
import com.example.messageapp.data.network.model.NewsResponse
import com.example.messageapp.data.network.model.Token
import com.example.messageapp.data.network.model.User
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.domain.repo.apiRepository.ApiRepository
import com.example.messageapp.store.SharedPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import javax.inject.Inject

class ApiServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService,
    private val sharedPreference: SharedPreference
) : ApiRepository {

    override suspend fun addUser(user: User): Response<LoginResponse> {
        val response = apiService.addUser(user)
        try {

            if (response.isSuccessful) {
                sharedPreference.save("tokenJWT", response.body()?.token!!)
                Log.d("TAG", "Token: ${SharedPreference(context).getValueString("tokenJWT")}")
            } else {
                Log.e("TAG", "Ошибка: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.d("TAG", e.toString())
        }
        return response
    }

    override suspend fun findUser(token: Token): Result<User> = try {
        Result.success(apiService.findUser(token))
    } catch (e: Exception) {
        Result.failure(e)
    }


    override suspend fun findUserByName(username: UserRequest): Result<UserResponse> = try {
            Result.success(apiService.findUserByName(username))
       }catch (e: Exception){
           Result.failure(e)
       }


    override suspend fun allUser(): Result<MutableList<UserResponse>> = try {
        Result.success(apiService.allUser())
    }catch (e: Exception){
        Result.failure(e)
    }

    override suspend fun findUserByStr(userName: UserRequest): Result<List<UserResponse>> = try {
        Result.success(apiService.findUserByStr(userName))
    }catch (e: Exception){
        Result.failure(e)
    }

    override suspend fun loginUser(loginRequest: LoginRequest): Result<User> = try {
        Result.success(apiService.loginUser(loginRequest))
    }catch (e: Exception){
        Result.failure(e)
    }

//    override suspend fun sendImage( image: MultipartBody.Part, newsRequest: NewsRequest) {
//        apiService.sendImage(newsRequest)
//    }

    override suspend fun addNews(newsRequest: NewsRequest) {
        apiService.addNews(newsRequest)
    }

    override suspend fun uploadNews(
        part: MultipartBody.Part,
        nameNews: RequestBody,
        userName: RequestBody
    ) {
       apiService.uploadNews(part, nameNews, userName)
    }
    override suspend fun allNews(): List<NewsResponse> = apiService.allNews()

}