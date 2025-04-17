package com.example.messageapp.data.network.webSocket

import com.example.messageapp.data.network.webSocket.client.WebSocketServiceImpl

class WebSocket(

) {


    suspend fun connect(userName: String) = WebSocketServiceImpl.connect(userName)

    suspend fun disconnect() = WebSocketServiceImpl.disconnect()

    suspend fun send(message: String) = WebSocketServiceImpl.send(message)


    suspend fun receive(message: String) = WebSocketServiceImpl.receive(message)
}