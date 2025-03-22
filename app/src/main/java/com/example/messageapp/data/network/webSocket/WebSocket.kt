package com.example.messageapp.data.network.webSocket

import com.example.messageapp.data.model.Message
import com.example.messageapp.data.network.webSocket.client.WebSocketServiceImpl

class WebSocket(

) {


    fun connect() = WebSocketServiceImpl.connect()

    fun disconnect() = WebSocketServiceImpl.disconnect()

    fun send (message: String) = WebSocketServiceImpl.send(message)

    fun receive(message: Message) = WebSocketServiceImpl.receive(message)
}