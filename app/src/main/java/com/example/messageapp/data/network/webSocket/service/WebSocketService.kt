package com.example.messageapp.data.network.webSocket.service

import com.example.messageapp.data.model.Message

interface WebSocketService {
    fun connect()
    fun disconnect()
    fun send(message: String): String
    fun receive(data: Message): Message
}