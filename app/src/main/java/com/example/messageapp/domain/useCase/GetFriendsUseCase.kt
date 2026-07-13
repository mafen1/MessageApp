package com.example.messageapp.domain.usecase

import com.example.messageapp.domain.repository.FriendRepository

class GetFriendsUseCase constructor(
    private val friendRepository: FriendRepository
) {
    suspend operator fun invoke(username: String): Result<List<String>> {
        return friendRepository.getFriends(username)
    }
}
