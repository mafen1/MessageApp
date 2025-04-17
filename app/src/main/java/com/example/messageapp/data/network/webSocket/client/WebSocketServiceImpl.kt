package com.example.messageapp.data.network.webSocket.client

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.messageapp.data.network.webSocket.service.WebSocketService
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

// синглтон
object WebSocketServiceImpl : WebSocketService {
    private val client = OkHttpClient()
    private var webSocket: WebSocket? = null
    private const val webSocketUrl = "ws://10.0.2.2:8081"
    private var isConnected = false

    private val _receivedMessage = MutableLiveData<String>()
    val receivedMessage: LiveData<String> = _receivedMessage


    override suspend fun connect(username: String) {
        try {
            val request = Request.Builder()
                .url("$webSocketUrl/friendRequest/$username")
                .build()
            println("Подключение к ${request.url}")



            webSocket = client.newWebSocket(request, object : WebSocketListener() {

                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.d("TAG", "Connected to WebSocket")
                    isConnected = true
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                        _receivedMessage.postValue(text)
                        Log.d("TAG", text)


                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    webSocket.close(code, null)
//                    Log.d("TAG", "closingWEBSOCKET")

                }

                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: Response?
                ) {
                    t.printStackTrace()
//                    Log.d("TAG", t.message.toString())
                }

            })
        } catch (e: Exception) {
            Log.e("WebSocket", "Ошибка подключения: ${e.message}", e)
            isConnected = false

        }
    }

    override suspend fun disconnect() {
        webSocket?.close(1000, "Closing connection")
    }

    override suspend fun send(message: String): String {
        webSocket?.send(message)

        return message
    }

    override suspend fun receive(data: String): String {
        return data
    }


}