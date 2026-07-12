package com.example.messageapp.ui.chatScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.BuildConfig
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.core.logD
import com.example.messageapp.data.network.model.Message
import com.example.messageapp.data.network.model.User
import com.example.messageapp.data.network.webSocket.client.ChatWebSocketClient
import com.example.messageapp.domain.useCase.ApiServiceUseCase
import com.example.messageapp.domain.useCase.AppPreferencesUseCase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import java.net.URI
import javax.inject.Inject
import kotlin.jvm.Synchronized

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

    private val _messageList: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())
    var messageList: StateFlow<List<Message>> = _messageList

    private val _error: MutableStateFlow<String?> = MutableStateFlow(null)
    var error: StateFlow<String?> = _error

    private var currentUserName: String = ""
    private var activePeerUserName: String = ""
    private var activeChatCacheKey: String? = null
    private val gson = Gson()
    private val messageListType = object : TypeToken<List<Message>>() {}.type


    @Synchronized
    fun disconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                webSocketClient?.disconnect()
            } catch (e: Exception) {
                Log.e("ChatVM", "Error disconnecting WebSocket", e)
            }
            webSocketClient = null
        }
    }

    @Synchronized
    fun connect(userName: String) {
        try {
            currentUserName = userName
            webSocketClient?.disconnect()
            val token = runBlocking {
                appPreference.getString(ConstVariables.tokenJWT).first()
            }
            val serverUri = URI("${BuildConfig.WS_URL}/chat/$userName?token=$token")

            logD("web socket username: ${userName}")

            webSocketClient = ChatWebSocketClient(serverUri) { message ->
                viewModelScope.launch(Dispatchers.Main) {
                    val parts = message.split(":", limit = 3)
                    if (parts.size >= 3) {
                        val senderUsername = parts[0]
                        val messageType = parts[1]
                        val messageContent = parts[2]
                        if (!isMessageForActiveChat(senderUsername)) return@launch
                        val isFromMe = senderUsername == currentUserName
                        updateMessageList(Message(messageContent, isFromMe, messageType))
                        logD("Получено сообщение от $senderUsername, isFromMe: $isFromMe")
                    } else if (parts.size >= 2) {
                        val senderUsername = parts[0]
                        val messageContent = parts[1]
                        if (!isMessageForActiveChat(senderUsername)) return@launch
                        val isFromMe = senderUsername == currentUserName
                        updateMessageList(Message(messageContent, isFromMe))
                    } else {
                        updateMessageList(Message(message, false))
                    }
                }
            }

            webSocketClient?.connect()
            logD("connect web socket")
        } catch (e: Exception) {
            logD("log web socket: ${e.toString()}")
            _error.value = "WebSocket error: ${e.message}"
        }

    }


    suspend fun findUserName(): String {
        return withContext(Dispatchers.IO) {
            appPreference.getString(ConstVariables.userName).first()
        }
    }

    fun updateMessageList(message: Message) {
        _messageList.update { currentList ->
            val newList = currentList + message
            saveActiveChat(newList)
            newList
        }
    }

    fun loadMessageHistory(currentUser: String, otherUser: String) {
        viewModelScope.launch(Dispatchers.IO) {
            activePeerUserName = otherUser
            activeChatCacheKey = chatCacheKey(currentUser, otherUser)
            val cachedMessages = readCachedMessages(activeChatCacheKey.orEmpty())
            if (cachedMessages.isNotEmpty()) {
                _messageList.value = cachedMessages
            }

            try {
                val result = apiUseCase.getMessages(currentUser, otherUser)
                val messages = result.getOrNull() ?: emptyList()
                logD("Loaded ${messages.size} messages between $currentUser and $otherUser")

                val messageList = messages.map { msg ->
                    val isSentByMe = msg.senderUsername == currentUser
                    Message(msg.message, isSentByMe, msg.messageType ?: "text")
                }

                _messageList.value = messageList
                saveCachedMessages(activeChatCacheKey.orEmpty(), messageList)
            } catch (e: Exception) {
                Log.e("ChatVM", "Error loading message history", e)
                _error.value = "Error loading history: ${e.message}"
                if (_messageList.value.isEmpty()) {
                    _messageList.value = emptyList()
                }
            }
        }
    }

    fun sendImageMessage(targetUsername: String, imagePart: MultipartBody.Part) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiUseCase.uploadMessageImage(imagePart)
            val fileName = response.getOrNull()?.fileName ?: return@launch
            webSocketClient?.sendMessage("to:$targetUsername:image:$fileName")
        }
    }

    private fun isMessageForActiveChat(senderUsername: String): Boolean {
        return activePeerUserName.isBlank() ||
            senderUsername == activePeerUserName ||
            senderUsername == currentUserName
    }

    private fun chatCacheKey(user1: String, user2: String): String {
        val users = listOf(user1, user2).sorted().joinToString(separator = "__") { username ->
            username.replace(Regex("[^A-Za-z0-9_@.-]"), "_")
        }
        return "chat_history_$users"
    }

    private suspend fun readCachedMessages(key: String): List<Message> {
        if (key.isBlank()) return emptyList()
        val cached = appPreference.getString(key).first()
        if (cached.isBlank()) return emptyList()

        return runCatching {
            gson.fromJson<List<Message>>(cached, messageListType).orEmpty()
        }.getOrElse { error ->
            Log.e("ChatVM", "Error reading cached chat", error)
            emptyList()
        }
    }

    private fun saveActiveChat(messages: List<Message>) {
        val key = activeChatCacheKey ?: return
        viewModelScope.launch(Dispatchers.IO) {
            saveCachedMessages(key, messages)
        }
    }

    private suspend fun saveCachedMessages(key: String, messages: List<Message>) {
        if (key.isBlank()) return
        appPreference.setString(key, gson.toJson(messages))
    }

    fun findUser() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val fetchedUser = apiUseCase.getCurrentUser().getOrNull()
                if (fetchedUser != null) {
                    _user.value = fetchedUser
                    logD(fetchedUser.toString())
                }
            } catch (e: Exception) {
                Log.e("ChatVM", "Error finding current user", e)
                _error.value = "Error finding user: ${e.message}"
            }
        }
    }

    fun resetError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }



}
