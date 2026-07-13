package com.example.messageapp.domain.repository

import com.example.messageapp.domain.model.User

interface UserRepository {
    suspend fun findUserByName(username: String): Result<User>
    suspend fun findUsersByString(query: String): Result<List<User>>
    suspend fun getAllUsers(): Result<List<User>>
    suspend fun updateProfile(userName: String, name: String, password: String?): Result<User>
}
