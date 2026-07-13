package com.example.messageapp.domain.usecase

import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.repository.UserRepository

class UpdateProfileUseCase constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userName: String, name: String, password: String?): Result<User> {
        return userRepository.updateProfile(userName, name, password)
    }
}
