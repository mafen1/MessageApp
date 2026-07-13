package com.example.messageapp.domain.usecase

import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.repository.UserRepository

class GetAllUsersUseCase constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<List<User>> {
        return userRepository.getAllUsers()
    }
}
