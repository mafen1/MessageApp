package com.example.messageapp.ui.chatScreen

import android.util.Log
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

    var webSocketClient: ChatWebSocketClient? = null

    private var _messageList: MutableStateFlow<MutableList<Message>?> = MutableStateFlow(null)
    var messageList: StateFlow<MutableList<Message>?> = _messageList

    private var currentUserName: String = ""


    fun disconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            webSocketClient?.disconnect()
        }
    }

    fun connect(userName: String) {
        try {
            currentUserName = userName
            val serverUri = URI("${ConstVariables.wsUrl}/chat/$userName")

            logD("web socket username: ${userName}")

            webSocketClient = ChatWebSocketClient(serverUri) { message ->
                viewModelScope.launch(Dispatchers.Main) {
                    // Формат сообщения от сервера: senderUsername:message
                    val parts = message.split(":", limit = 2)
                    if (parts.size >= 2) {
                        val senderUsername = parts[0]
                        val messageContent = parts[1]
                        val isFromMe = senderUsername == currentUserName
                        updateMessageList(Message(messageContent, isFromMe))
                        logD("Получено сообщение от $senderUsername, isFromMe: $isFromMe")
                    } else {
                        // Для обратной совместимости
                        updateMessageList(Message(message, false))
                    }
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
        val currentList = _messageList.value ?: mutableListOf()
        val newList = currentList.toMutableList()
        newList.add(message)
        _messageList.value = newList
    }

    fun loadMessageHistory(currentUser: String, otherUser: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = apiUseCase.getMessages(currentUser, otherUser)
                val messages = result.getOrNull() ?: emptyList()
                logD("Loaded ${messages.size} messages between $currentUser and $otherUser")

                val messageList = messages.map { msg ->
                    val isSentByMe = msg.senderUsername == currentUser
                    Message(msg.message, isSentByMe)
                }.toMutableList()

                _messageList.value = messageList
            } catch (e: Exception) {
                Log.e("ChatVM", "Error loading message history", e)
            }
        }
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
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }



}
