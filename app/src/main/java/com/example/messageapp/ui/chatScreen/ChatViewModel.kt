package com.example.messageapp.ui.chatScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.data.network.webSocket.WebSocket
import com.example.messageapp.data.network.webSocket.client.WebSocketServiceImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatViewModel() : ViewModel() {

    fun chat(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
                WebSocket().send(message)
        }
    }

    fun disconnect(){
        viewModelScope.launch(Dispatchers.IO) {
            WebSocket().disconnect()
        }
    }

    fun connect(){
        viewModelScope.launch(Dispatchers.IO) {
            WebSocket().connect()
        }
    }
}