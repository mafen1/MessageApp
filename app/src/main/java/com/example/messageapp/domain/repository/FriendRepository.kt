package com.example.messageapp.domain.repository

import com.example.messageapp.domain.model.FriendRequest

interface FriendRepository {
    suspend fun getFriends(username: String): Result<List<String>>
    suspend fun getFriendRequests(username: String): Result<List<FriendRequest>>
    suspend fun sendFriendRequest(senderUsername: String, receiverUsername: String): Result<String>
    suspend fun acceptFriendRequest(senderUsername: String, receiverUsername: String): Result<String>
    suspend fun rejectFriendRequest(senderUsername: String, receiverUsername: String): Result<String>
}
