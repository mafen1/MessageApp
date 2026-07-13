package com.example.messageapp.domain.repository

import com.example.messageapp.domain.model.LoggedInUser
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.model.UserCredentials

interface AuthRepository {
    suspend fun register(credentials: UserCredentials): Result<LoggedInUser>
    suspend fun login(credentials: UserCredentials): Result<LoggedInUser>
    suspend fun getCurrentUser(): Result<User>
}
