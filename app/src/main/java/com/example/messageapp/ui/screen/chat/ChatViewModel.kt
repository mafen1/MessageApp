package com.example.messageapp.ui.screen.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.core.logD
import com.example.messageapp.domain.model.Message
import com.example.messageapp.domain.model.MessageStatus
import com.example.messageapp.domain.model.SocketState
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.repository.ChatSocketRepository
import com.example.messageapp.domain.usecase.AppPreferencesUseCase
import com.example.messageapp.domain.usecase.GetChatHistoryUseCase
import com.example.messageapp.domain.usecase.UploadChatImageUseCase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val appPreference: AppPreferencesUseCase,
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val uploadChatImageUseCase: UploadChatImageUseCase,
    private val chatSocketRepository: ChatSocketRepository
) : ViewModel() {

    private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
    val user: StateFlow<User?> = _user

    private val _messageText: MutableStateFlow<String> = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText

    private val _messageList: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())
    val messageList: StateFlow<List<Message>> = _messageList

    private val _connectionState: MutableStateFlow<SocketState> = MutableStateFlow(SocketState.Disconnected)
    val connectionState: StateFlow<SocketState> = _connectionState

    private val _error: MutableStateFlow<String?> = MutableStateFlow(null)
    val error: StateFlow<String?> = _error

    private var currentUserName: String = ""
    private var activePeerUserName: String = ""
    private var activeChatCacheKey: String? = null
    private val gson = Gson()
    private val messageListType = object : TypeToken<List<Message>>() {}.type

    init {
        chatSocketRepository.observeMessages()
            .onEach { message ->
                if (isMessageForActiveChat(message.senderUsername)) {
                    updateMessageList(message)
                }
            }
            .launchIn(viewModelScope)

        chatSocketRepository.connectionState
            .onEach { _connectionState.value = it }
            .launchIn(viewModelScope)
    }

    fun connect(userName: String) {
        currentUserName = userName
        val token = runBlocking {
            appPreference.getString(ConstVariables.tokenJWT).first()
        }
        chatSocketRepository.connect(userName, token)
    }

    fun disconnect() {
        chatSocketRepository.disconnect()
    }

    suspend fun findUserName(): String {
        return withContext(Dispatchers.IO) {
            appPreference.getString(ConstVariables.userName).first()
        }
    }

    fun updateMessageList(message: Message) {
        _messageList.update { currentList ->
            val filtered = currentList.filter { it.clientMessageId.isBlank() || it.clientMessageId != message.clientMessageId }
            val newList = filtered + message
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
                val result = getChatHistoryUseCase(currentUser, otherUser)
                val messages = result.getOrNull() ?: emptyList()
                logD("Loaded ${messages.size} messages between $currentUser and $otherUser")

                _messageList.value = messages
                saveCachedMessages(activeChatCacheKey.orEmpty(), messages)
            } catch (e: Exception) {
                Log.e("ChatVM", "Error loading message history", e)
                _error.value = "Error loading history: ${e.message}"
                if (_messageList.value.isEmpty()) {
                    _messageList.value = emptyList()
                }
            }
        }
    }

    fun sendTextMessage(targetUsername: String, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val message = Message(
                senderUsername = currentUserName,
                recipientUsername = targetUsername,
                text = text,
                isFromMe = true,
                type = "text",
                status = MessageStatus.SENDING
            )
            updateMessageList(message)
            chatSocketRepository.sendMessage(message)
        }
    }

    fun sendImageMessage(targetUsername: String, imageBytes: ByteArray) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = uploadChatImageUseCase(imageBytes)
            val fileName = response.getOrNull() ?: return@launch
            val message = Message(
                senderUsername = currentUserName,
                recipientUsername = targetUsername,
                text = fileName,
                isFromMe = true,
                type = "image",
                status = MessageStatus.SENDING
            )
            updateMessageList(message)
            chatSocketRepository.sendMessage(message)
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
                _user.value = null
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
