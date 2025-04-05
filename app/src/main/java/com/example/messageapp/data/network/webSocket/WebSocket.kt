package com.example.messageapp.data.network.webSocket

import com.example.messageapp.data.network.model.Message
import com.example.messageapp.data.network.webSocket.client.WebSocketServiceImpl

class WebSocket(

) {


    suspend fun connect() = WebSocketServiceImpl.connect()

    suspend fun disconnect() = WebSocketServiceImpl.disconnect()

    suspend fun send (message: String) = WebSocketServiceImpl.send(message)

    suspend fun receive(message: Message) = WebSocketServiceImpl.receive(message)
}