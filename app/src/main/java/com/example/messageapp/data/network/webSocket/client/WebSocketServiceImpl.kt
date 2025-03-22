package com.example.messageapp.data.network.webSocket.client

import android.util.Log
import com.example.messageapp.data.model.Message
import com.example.messageapp.data.network.webSocket.service.WebSocketService
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

// синглтон
// todo сделать приватными поля
object WebSocketServiceImpl : WebSocketService {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private const val webSocketUrl = "ws://10.0.2.2:8081"
    private var isConnected = false

    override fun connect() {
        try {
            val request = Request.Builder()
                .url("$webSocketUrl/chat")
                .build()

            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.d("TAG", "Connected to WebSocket")
                    isConnected = true
                    Log.d("TAG", isConnected.toString())
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    if (isConnected) {
                        println("Received message: $text")
                        val message = text.split(",")
                        receive(
                            Message(
                                id = message[0].toInt(),
                                name = message[1],
                                message = message[0]
                            )
                        )
                    }
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    webSocket.close(code, null)
                    Log.d("TAG", "closingWEBSOCKET")
                    println("Closing : $code / $reason")
                }

                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: Response?
                ) {
                    t.printStackTrace()
                }

            })
        } catch (e: Exception) {
            Log.e("WebSocket", "Ошибка подключения: ${e.message}", e)
            isConnected = false

        }
    }

    override fun disconnect() {
        webSocket?.close(1000, "Closing connection")
    }

    override fun send(message: String): String {
        webSocket?.send(message)
        return message
    }

    override fun receive(data: Message): Message {
        return data
    }


}