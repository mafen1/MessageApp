package com.example.messageapp.data.repository

import com.example.messageapp.data.mapper.toDomain
import com.example.messageapp.data.network.api.service.ApiService
import com.example.messageapp.data.network.model.UpdateProfileRequest
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserRepository {

    override suspend fun findUserByName(username: String): Result<User> = safeApiCall {
        apiService.findUserByName(UserRequest(username)).toDomain()
    }

    override suspend fun findUsersByString(query: String): Result<List<User>> = safeApiCall {
        apiService.findUserByStr(UserRequest(query)).map { it.toDomain() }
    }

    override suspend fun getAllUsers(): Result<List<User>> = safeApiCall {
        apiService.allUser().map { it.toDomain() }
    }

    override suspend fun updateProfile(
        userName: String,
        name: String,
        password: String?
    ): Result<User> = safeApiCall {
        apiService.updateProfile(UpdateProfileRequest(userName, name, password)).toDomain()
    }
}
