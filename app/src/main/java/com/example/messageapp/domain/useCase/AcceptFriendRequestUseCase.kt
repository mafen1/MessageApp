package com.example.messageapp.domain.usecase

import com.example.messageapp.domain.repository.FriendRepository

class AcceptFriendRequestUseCase constructor(
    private val friendRepository: FriendRepository
) {
    suspend operator fun invoke(senderUsername: String, receiverUsername: String): Result<String> {
        return friendRepository.acceptFriendRequest(senderUsername, receiverUsername)
    }
}
