package com.example.messageapp.domain.usecase

import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.repository.UserRepository

class SearchUsersUseCase constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(query: String): Result<List<User>> {
        return userRepository.findUsersByString(query)
    }
}
