package com.example.messageapp.data.network.webSocket

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class FriendWebSocketClient(
    serverUri: URI,
    private val messageListener: (String) -> Unit
) : WebSocketClient(serverUri) {

    override fun onOpen(handshakedata: ServerHandshake?) {
        Log.d("FriendWS", "opened")
    }

    override fun onMessage(message: String?) {
        message?.let(messageListener)
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d("FriendWS", "closed: $code $reason")
    }

    override fun onError(ex: Exception?) {
        Log.e("FriendWS", "error", ex)
    }
}
