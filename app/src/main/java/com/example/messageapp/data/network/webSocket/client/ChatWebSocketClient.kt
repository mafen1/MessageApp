package com.example.messageapp.data.network.webSocket.client

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.inject.Inject

class ChatWebSocketClient @Inject constructor(
    serverUri: URI,
    private val messageListener: (String) -> Unit
) : WebSocketClient(serverUri), com.example.messageapp.data.network.webSocket.client.WebSocketClient {

    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.d("TAG", "вызов функции on Open")
    }

    override fun onMessage(message: String?) {
        if (message != null) {
            messageListener.invoke(message)
            Log.d("TAG", message)
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d("TAG", code.toString())
    }

    override fun onError(ex: Exception?) {
        Log.e("TAG", ex.toString())
    }

    override fun sendMessage(message: String) {
        send(message)
    }

    override fun disconnect() {
        disconnect()
    }
}