package com.example.messageapp.domain.repoImpl

import android.content.Context
import android.util.Log
import com.example.messageapp.data.network.api.service.ApiService
import com.example.messageapp.data.network.model.LoginRequest
import com.example.messageapp.data.network.model.LoginResponse
import com.example.messageapp.data.network.model.Token
import com.example.messageapp.data.network.model.User
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.domain.repo.apiRepository.ApiRepository
import com.example.messageapp.store.SharedPreference
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Response
import javax.inject.Inject

class ApiServiceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val apiService: ApiService,
    private val sharedPreference: SharedPreference
) : ApiRepository {

    override suspend fun addUser(user: User): Response<LoginResponse> {
        try {
            val response = apiService.addUser(user)
            if (response.isSuccessful) {
                sharedPreference.save("tokenJWT", response.body()?.token!!)
                Log.d("TAG", "Token: ${SharedPreference(context).getValueString("tokenJWT")}")
            } else {
                Log.e("TAG", "Ошибка: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.d("TAG", e.toString())
        }
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

}