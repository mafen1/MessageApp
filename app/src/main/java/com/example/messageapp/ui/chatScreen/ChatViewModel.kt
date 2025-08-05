package com.example.messageapp.ui.chatScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.data.network.model.Message
import com.example.messageapp.data.network.webSocket.client.ChatWebSocketClient
import com.example.messageapp.domain.useCase.AppPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URI
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val appPreference: AppPreferencesUseCase,
) : ViewModel() {

//    private val _user:MutableLiveData<UserResponse> = MutableLiveData()
//    var user:LiveData<UserResponse> = _user

    private val _messageText:MutableLiveData<String> = MutableLiveData()
    var messageText:LiveData<String> = _messageText

    private var _webSocketClient: MutableLiveData<ChatWebSocketClient?> = MutableLiveData()
    var webSocketClient: LiveData<ChatWebSocketClient?> = _webSocketClient

    private var _messageList: MutableLiveData<MutableList<Message>> = MutableLiveData()
    var messageList : LiveData<MutableList<Message>> = _messageList



    fun disconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            _webSocketClient.value?.disconnect()
        }
    }

    fun connect(userName: String) {
        val serverUri = URI("ws://10.0.2.2:8081/chat/$userName")

        _webSocketClient.value = ChatWebSocketClient(serverUri) { message ->
            viewModelScope.launch(Dispatchers.Main) {
                updateMessageList(Message(message, false))
            }
        }

        _webSocketClient.value?.connect()

    }


    fun findUserName(): String {
        return appPreference.getValueString("username")
            ?: throw IllegalArgumentException("не найден пользователь с данным юзер неймом")
    }

    fun updateMessageList(message: Message) {
        val newList = _messageList.value?.toMutableList() ?: mutableListOf()
        newList.add(message)
        _messageList.value = newList
        Log.d("TAG", _messageList.value.toString())
    }


}
