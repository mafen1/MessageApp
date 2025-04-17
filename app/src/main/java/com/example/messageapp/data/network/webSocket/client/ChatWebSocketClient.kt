package com.example.messageapp.data.network.webSocket.client

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class ChatWebSocketClient (serverUri: URI, private val messageListener: (String) -> Unit) : WebSocketClient(serverUri) {
    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.d("TAG", "вызов функции on Open")
    }

    override fun onMessage(message: String?) {
        if (message != null) {
            messageListener.invoke(message)
//            Log.d("TAG", message)
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {

    }

    override fun onError(ex: Exception?) {

    }

    fun sendMessage(message: String){
        send(message)
    }

    fun disconnect(){
        disconnect()
    }
}