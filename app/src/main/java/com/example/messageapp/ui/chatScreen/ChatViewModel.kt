package com.example.messageapp.ui.chatScreen

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.data.network.model.Message
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.data.network.webSocket.client.ChatWebSocketClient
import com.example.messageapp.store.SharedPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URI
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor() : ViewModel() {

    val user = MutableLiveData<UserResponse>()
    val messageText = MutableLiveData<String>()
    var webSocketClient: ChatWebSocketClient? = null
    var messageList = MutableLiveData(mutableListOf<Message>())


    fun disconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            webSocketClient?.disconnect()
        }
    }

    fun connect(userName: String) {
        val serverUri = URI("ws://10.0.2.2:8081/chat/$userName")

        webSocketClient = ChatWebSocketClient(serverUri) { message ->
            viewModelScope.launch(Dispatchers.Main) {
//                Log.d("TAG", "Сообщение до: $message")
                updateMessageList(Message(message, false))
//                Log.d("TAG", "Сообщение после: $message") // Теперь должно сработать
            }
        }

        webSocketClient!!.connect()

    }


    fun findUserName(context: Context): String {
        return SharedPreference(context).getValueString("username")
            ?: throw IllegalArgumentException("не найден пользователь с данным юзер неймом")
    }

    fun updateMessageList(message: Message) {
        val newList = messageList.value?.toMutableList() ?: mutableListOf()
        newList.add(message)
        messageList.value = newList
        Log.d("TAG", messageList.value.toString())
    }


}
