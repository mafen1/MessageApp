package com.example.messageapp.domain.usecase

import com.example.messageapp.domain.model.FriendRequest
import com.example.messageapp.domain.repository.FriendRepository

class GetFriendRequestsUseCase constructor(
    private val friendRepository: FriendRepository
) {
    suspend operator fun invoke(username: String): Result<List<FriendRequest>> {
        return friendRepository.getFriendRequests(username)
    }
}
