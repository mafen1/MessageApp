package com.example.messageapp.domain.usecase

import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.repository.AuthRepository

class GetCurrentUserUseCase constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<User> {
        return authRepository.getCurrentUser()
    }
}
