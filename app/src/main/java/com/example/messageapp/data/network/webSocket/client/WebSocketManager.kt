package com.example.messageapp.data.network.webSocket.client

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketManager() {
    private val client = OkHttpClient()
     var webSocket: WebSocket? = null
    private val webSocketUrl = "ws://10.0.2.2:8081"




    fun connect() {
        val request = Request.Builder()
            .url("$webSocketUrl" + "/chat")
            .build()

        webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("TAG", "Connected to WebSocket")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                println("Received message: $text")
                Log.d("TAG", text)
                webSocket.send(text)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(code, null)
                println("Closing : $code / $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                t.printStackTrace()
            }
        })

    }

    fun disconnect() {
        webSocket?.close(1000, "Closing connection")
    }


}