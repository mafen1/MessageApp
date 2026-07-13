package com.example.messageapp.domain.usecase

import com.example.messageapp.domain.model.LoggedInUser
import com.example.messageapp.domain.model.UserCredentials
import com.example.messageapp.domain.repository.AuthRepository

class RegisterUseCase constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(credentials: UserCredentials): Result<LoggedInUser> {
        return authRepository.register(credentials)
    }
}
