package com.example.messageapp.ui.chatScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.data.network.webSocket.client.WebSocketManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel() : ViewModel() {

    fun chat(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            WebSocketManager().webSocket?.send(message)
            WebSocketManager().webSocket?.close(1000, "")

        }
    }

    fun disconnect(){
        viewModelScope.launch(Dispatchers.IO) {
            WebSocketManager().disconnect()
        }
    }
}