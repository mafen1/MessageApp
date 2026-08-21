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
        viewModelScope.launch(Dispatchers.IO) {
            currentUserName = userName
            val token = appPreference.getString(ConstVariables.tokenJWT).first()
            chatSocketRepository.connect(userName, token)
        }
    }

    fun disconnect() {
        chatSocketRepository.disconnect()
    }

    fun updateMessageList(message: Message) {
        _messageList.update { currentList ->
            val filtered = currentList.filter { existing ->
                when {
                    // точный дедуп по id
                    message.clientMessageId.isNotBlank() && existing.clientMessageId == message.clientMessageId -> false
                    // вытесняем stale blank-id оптимистичную заглушку тем же содержимым (фикс дублей у отправителя)
                    existing.clientMessageId.isBlank() && message.isFromMe &&
                        existing.text == message.text &&
                        existing.recipientUsername == message.recipientUsername -> false
                    else -> true
                }
            }
            filtered + message
        }
        saveMessage(message)
    }

    private fun saveMessage(message: Message) {
        if (currentUserName.isBlank()) return
        val other = if (message.isFromMe) message.recipientUsername else message.senderUsername
        val peer = other.takeIf { it.isNotBlank() } ?: activePeerUserName
        if (peer.isBlank()) return
        viewModelScope.launch(Dispatchers.IO) {
            saveMessageUseCase(message, chatId(currentUserName, peer))
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
                clientMessageId = java.util.UUID.randomUUID().toString(),
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
                clientMessageId = java.util.UUID.randomUUID().toString(),
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

    fun resetError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}
