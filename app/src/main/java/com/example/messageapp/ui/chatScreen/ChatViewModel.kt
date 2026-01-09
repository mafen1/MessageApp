package com.example.messageapp.ui.chatScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.core.logD
import com.example.messageapp.data.network.model.Message
import com.example.messageapp.data.network.model.Token
import com.example.messageapp.data.network.model.User
import com.example.messageapp.data.network.webSocket.client.ChatWebSocketClient
import com.example.messageapp.domain.useCase.ApiServiceUseCase
import com.example.messageapp.domain.useCase.AppPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.net.URI
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val appPreference: AppPreferencesUseCase,
    private val apiUseCase: ApiServiceUseCase
) : ViewModel() {

    private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
    var user: StateFlow<User?> = _user

    private val _messageText: MutableLiveData<String> = MutableLiveData()
    var messageText: LiveData<String> = _messageText

    var webSocketClient: ChatWebSocketClient? = null


//    private var _webSocketClient: MutableLiveData<ChatWebSocketClient?> = MutableLiveData()
//    var webSocketClient: LiveData<ChatWebSocketClient?> = _webSocketClient

    private var _messageList: MutableLiveData<MutableList<Message>> = MutableLiveData()
    var messageList: LiveData<MutableList<Message>> = _messageList


    fun disconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            webSocketClient?.disconnect()
        }
    }

    fun connect(userName: String) {
        try {
            val serverUri = URI("${ConstVariables.wsUrl}/chat/$userName")
            // для закрытия старого соединения
//        webSocketClient?.disconnect()

            webSocketClient = ChatWebSocketClient(serverUri) { message ->
                viewModelScope.launch(Dispatchers.Main) {
                    updateMessageList(Message(message, false))
                }
            }

            webSocketClient?.connect()
            logD("connect web socket")
        } catch (e: Exception) {
            logD(e.toString())
        }

    }


    fun findUserName(): String {
        return appPreference.getString(ConstVariables.userName).toString()
    }

    fun updateMessageList(message: Message) {
        // Инициализируем пустой список, если он null
        val currentList = _messageList.value ?: mutableListOf()
        val newList = currentList.toMutableList()
        newList.add(message)
        _messageList.value = newList
    }

    fun findUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = Token(appPreference.getString(ConstVariables.tokenJWT).first())
                logD(token.toString())
                logD(appPreference.getString(ConstVariables.tokenJWT).first())
                if (apiUseCase.findUser(token).getOrNull() != null) {
                    _user.value = (apiUseCase.findUser(token).getOrNull())
                    logD(_user.value.toString())
                }else{

                }
            } catch (e: Exception) {
//                Log.d(ConstVariables.LoggerDebugTag, e.toString())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }



}
