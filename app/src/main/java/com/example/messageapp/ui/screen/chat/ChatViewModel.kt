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
import com.example.messageapp.domain.usecase.SaveMessageUseCase
import com.example.messageapp.domain.usecase.UploadChatImageUseCase
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
    private val saveMessageUseCase: SaveMessageUseCase,
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
            saveMessage(message)
            newList
        }
    }

    private fun saveMessage(message: Message) {
        if (currentUserName.isBlank() || activePeerUserName.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            saveMessageUseCase(message, chatId(currentUserName, activePeerUserName))
        }
    }

    fun loadMessageHistory(currentUser: String, otherUser: String) {
        viewModelScope.launch(Dispatchers.IO) {
            currentUserName = currentUser
            activePeerUserName = otherUser

            try {
                val result = getChatHistoryUseCase(currentUser, otherUser)
                val messages = result.getOrNull() ?: emptyList()
                logD("Loaded ${messages.size} messages between $currentUser and $otherUser")
                _messageList.value = messages
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

    private fun chatId(user1: String, user2: String): String {
        return listOf(user1, user2).sorted().joinToString("__")
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
