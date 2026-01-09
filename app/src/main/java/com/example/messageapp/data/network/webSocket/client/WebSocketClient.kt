package com.example.messageapp.data.network.webSocket.client

interface WebSocketClient {
    fun sendMessage(message: String)
    fun disconnect()
}