package com.example.messageapp.data.repository

import com.example.messageapp.data.mapper.toDomain
import com.example.messageapp.data.mapper.toLoginRequest
import com.example.messageapp.data.mapper.toRegisterDto
import com.example.messageapp.data.network.api.service.ApiService
import com.example.messageapp.domain.model.LoggedInUser
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.model.UserCredentials
import com.example.messageapp.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AuthRepository {

    override suspend fun register(credentials: UserCredentials): Result<LoggedInUser> = safeApiCall {
        apiService.addUser(credentials.toRegisterDto()).toDomain()
    }

    override suspend fun login(credentials: UserCredentials): Result<LoggedInUser> = safeApiCall {
        apiService.loginUser(credentials.toLoginRequest()).toDomain()
    }

    override suspend fun getCurrentUser(): Result<User> = safeApiCall {
        apiService.getCurrentUser().toDomain()
    }
}
