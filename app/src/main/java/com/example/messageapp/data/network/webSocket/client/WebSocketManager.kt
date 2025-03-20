package com.example.messageapp.data.network.webSocket.client

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

// синглтон
// todo сделать приватными поля
object WebSocketManager {
    val client = OkHttpClient()
    var webSocket: WebSocket? = null
    private val webSocketUrl = "ws://10.0.2.2:8081"
    var isConnected = false


    fun connect() {

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
                    println("Received message: $text")
                    Log.d("TAG", text)
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

    fun disconnect() {
        webSocket?.close(1000, "Closing connection")
    }


}