package com.example.messageapp.domain.repository

import com.example.messageapp.domain.model.Message
import com.example.messageapp.domain.model.SocketState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ChatSocketRepository {
    val connectionState: StateFlow<SocketState>
    fun connect(userName: String, token: String)
    fun disconnect()
    suspend fun sendMessage(message: Message)
    fun observeMessages(): Flow<Message>
}
