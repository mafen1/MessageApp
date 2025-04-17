package com.example.messageapp.data.network.webSocket.service

interface WebSocketService {
    suspend fun connect(username: String)
    suspend fun disconnect()
    suspend fun send(message: String): String
    suspend fun receive(data: String): String
}