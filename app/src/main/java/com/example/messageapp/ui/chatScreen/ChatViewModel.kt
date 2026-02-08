package com.example.messageapp.ui.chatScreen

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
import kotlinx.coroutines.withContext
import java.net.URI
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val appPreference: AppPreferencesUseCase,
    private val apiUseCase: ApiServiceUseCase
) : ViewModel() {

    private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
    var user: StateFlow<User?> = _user

    private val _messageText: MutableStateFlow<String> = MutableStateFlow("")
    var messageText: StateFlow<String> = _messageText

    // todo .
    var webSocketClient: ChatWebSocketClient? = null

    private var _messageList: MutableStateFlow<MutableList<Message>?> = MutableStateFlow(null)
    var messageList: StateFlow<MutableList<Message>?> = _messageList


    fun disconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            webSocketClient?.disconnect()
        }
    }

    fun connect(userName: String) {
        try {
            val serverUri = URI("${ConstVariables.wsUrl}/chat/$userName")

            logD("web socket username: ${userName}")

            webSocketClient = ChatWebSocketClient(serverUri) { message ->
                viewModelScope.launch(Dispatchers.Main) {
                    updateMessageList(Message(message, false))
                }
            }

            webSocketClient?.connect()
            logD("connect web socket")
        } catch (e: Exception) {
            logD("log web socket: ${e.toString()}")

        }

    }


    suspend fun findUserName(): String {
        return withContext(Dispatchers.IO) {
            appPreference.getString(ConstVariables.userName).first()
        }
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
                if (apiUseCase.fetchUser(token).getOrNull() != null) {
                    _user.value = (apiUseCase.fetchUser(token).getOrNull())
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
