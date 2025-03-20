package com.example.messageapp.ui.chatScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.data.network.webSocket.client.WebSocketManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel() : ViewModel() {

    fun chat(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (WebSocketManager.isConnected) {
                WebSocketManager.webSocket?.send(message)
            }

        }
    }

    fun disconnect(){
        viewModelScope.launch(Dispatchers.IO) {
            WebSocketManager.disconnect()
        }
    }
}