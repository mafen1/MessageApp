package com.example.messageapp.data.network.webSocket.service

import com.example.messageapp.data.network.model.Message

interface WebSocketService {
    suspend fun connect()
    suspend fun disconnect()
    suspend fun send(message: String): String
    suspend fun receive(data: Message): Message
}