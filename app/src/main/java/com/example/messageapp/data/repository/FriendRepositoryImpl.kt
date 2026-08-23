package com.example.messageapp.data.repository

import com.example.messageapp.data.mapper.toDomain
import com.example.messageapp.data.network.api.service.ApiService
import com.example.messageapp.data.network.model.AcceptFriendRequest
import com.example.messageapp.data.network.model.FriendRequest as FriendRequestDto
import com.example.messageapp.domain.model.FriendRequest
import com.example.messageapp.domain.repository.FriendRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : FriendRepository {

    override suspend fun getFriends(username: String): Result<List<String>> = safeApiCall {
        apiService.getFriends(username).friends ?: emptyList()
    }

    override suspend fun getFriendRequests(username: String): Result<List<FriendRequest>> = safeApiCall {
        apiService.getFriendRequests(username).requests?.map { it.toDomain() } ?: emptyList()
    }

    override suspend fun sendFriendRequest(
        senderUsername: String,
        receiverUsername: String
    ): Result<String> = safeApiCall {
        apiService.sendFriendRequest(
            FriendRequestDto(
                senderUserName = senderUsername,
                receiverUserName = receiverUsername,
                status = "pending"
            )
        ).message
    }

    override suspend fun acceptFriendRequest(
        senderUsername: String,
        receiverUsername: String
    ): Result<String> = safeApiCall {
        apiService.acceptFriend(
            AcceptFriendRequest(senderUsername, receiverUsername)
        ).message
    }

    override suspend fun rejectFriendRequest(
        senderUsername: String,
        receiverUsername: String
    ): Result<String> = safeApiCall {
        apiService.rejectFriend(
            AcceptFriendRequest(senderUsername, receiverUsername)
        ).message
    }
}
