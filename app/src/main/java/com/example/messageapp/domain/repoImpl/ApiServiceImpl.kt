package com.example.messageapp.domain.repoImpl

import android.util.Log
import android.util.MalformedJsonException
import com.example.messageapp.core.logD
import com.example.messageapp.data.network.api.service.ApiService
import com.example.messageapp.data.network.model.LoginRequest
import com.example.messageapp.data.network.model.LoginResponse
import com.example.messageapp.data.network.model.NewsResponse
import com.example.messageapp.data.network.model.Token
import com.example.messageapp.data.network.model.User
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.domain.repo.apiRepository.ApiRepository
import com.google.gson.Gson
import com.google.gson.JsonParseException
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import javax.inject.Inject

class ApiServiceImpl @Inject constructor(
    private val apiService: ApiService,
) : ApiRepository {

    override suspend fun addUser(user: User): Result<LoginResponse> {
        return safeApiCall {  apiService.addUser(user) }
    }

    override suspend fun findUser(token: Token): Result<User> {
//        logD("${apiService.findUser(token)} hfdhjfhjdfjdhf")
        return safeApiCall { apiService.findUser(token) }
    }


    override suspend fun findUserByName(username: UserRequest): Result<UserResponse> {
        return safeApiCall { apiService.findUserByName(username) }
    }


    override suspend fun allUser(): Result<MutableList<UserResponse>> {
        return safeApiCall { apiService.allUser() }
    }

    override suspend fun findUserByStr(userName: UserRequest): Result<List<UserResponse>> {
        return safeApiCall { apiService.findUserByStr(userName) }
    }

    override suspend fun loginUser(loginRequest: LoginRequest): Result<User> {
        return safeApiCall { apiService.loginUser(loginRequest) }
    }


    override suspend fun uploadNews(
        part: MultipartBody.Part,
        nameNews: RequestBody,
        userName: RequestBody
    ) {
        safeApiCall { apiService.uploadNews(part, nameNews, userName) }
    }

    override suspend fun allNews(): List<NewsResponse> = apiService.allNews()

    ///
    override suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return try {
            val result = apiCall()
            Log.d("API_SUCCESS", "Response: ${Gson().toJson(result)}")
            Result.success(result)
        } catch (e: Exception) {
            // Пытаемся получить raw response при ошибке
            if (e is JsonParseException || e is MalformedJsonException) {
                Log.e("JSON_ERROR", "Malformed JSON received. Check network logs for raw response", e)
                // Здесь можно попытаться получить raw response из стека
            }
            Log.e("API_ERROR", "API call failed: ${e.message}", e)
            Result.failure(e)
        }
    }

}
