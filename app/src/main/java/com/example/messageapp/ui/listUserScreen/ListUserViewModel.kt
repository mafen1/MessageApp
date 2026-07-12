package com.example.messageapp.ui.listUserScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.BuildConfig
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.core.logD
import com.example.messageapp.data.network.model.AcceptFriendRequest
import com.example.messageapp.data.network.model.FriendRequest
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.data.network.webSocket.client.ChatWebSocketClient
import com.example.messageapp.domain.useCase.ApiServiceUseCase
import com.example.messageapp.domain.useCase.AppPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.URI
import javax.inject.Inject

@HiltViewModel
class ListUserViewModel @Inject constructor(
    private val appPreference: AppPreferencesUseCase,
    private val apiServiceUseCase: ApiServiceUseCase
) : ViewModel() {

    private var _foundUsers = MutableStateFlow<List<UserResponse>>(emptyList())
    var foundUser: StateFlow<List<UserResponse>> = _foundUsers

    private val _messageNotification = MutableStateFlow("")
    var messageNotification: StateFlow<String> = _messageNotification

    private val _pendingRequests = MutableStateFlow<Set<String>>(emptySet())
    val pendingRequests: StateFlow<Set<String>> = _pendingRequests

    private val _friendRequestResult = MutableStateFlow<String?>(null)
    val friendRequestResult: StateFlow<String?> = _friendRequestResult

    private var _webSocketClient: ChatWebSocketClient? = null

    private var _pollingJob: Job? = null
    private var _currentUserName = ""
    private var _isPollingStarted = false

    fun startPolling(userName: String) {
        if (userName.isBlank()) return
        if (_isPollingStarted && _currentUserName == userName) return
        stopPolling()
        _isPollingStarted = true
        _currentUserName = userName
        _pollingJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                try {
                    val result = apiServiceUseCase.getFriendRequests(userName)
                    if (result.isSuccess) {
                        val requests = result.getOrNull()?.requests ?: emptyList()
                        if (requests.isNotEmpty()) {
                            val sender = requests.last().senderUserName
                            logD("Polling: found friend request from $sender")
                            _messageNotification.value = "Заявка от $sender"
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ListUserVM", "Polling error: ${e.message}")
                }
                delay(3000)
            }
        }
    }

    fun stopPolling() {
        _pollingJob?.cancel()
        _pollingJob = null
        _isPollingStarted = false
    }

    fun findUserByStr(userName: String, currentUserName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val users = apiServiceUseCase.findUserByStr(UserRequest(userName))
                val filtered = users.getOrThrow().filter { it.username != currentUserName }
                _foundUsers.value = filtered
            } catch (e: Exception) {
                Log.e("ListUserViewModel", "Error finding user by string", e)
            }
        }
    }

    fun clearSearchResults() {
        _foundUsers.value = emptyList()
    }

    fun searchUser(userName: UserRequest, currentUserName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = apiServiceUseCase.findUserByName(userName)
                val u = user.getOrThrow()
                if (u.username != currentUserName) {
                    _foundUsers.value = listOf(u)
                } else {
                    _foundUsers.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("ListUserViewModel", "Error searching user", e)
            }
        }
    }

    fun connectWebSocket(userName: String) {
        logD("Connecting WebSocket for: $userName")
        val token = runBlocking {
            appPreference.getString(ConstVariables.tokenJWT).first()
        }
        val serverUri = URI("${BuildConfig.WS_URL}/friendMessage/$userName?token=$token")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val client = ChatWebSocketClient(serverUri) { message ->
                    logD("WebSocket received message: '$message'")
                    _messageNotification.value = message
                    logD("messageNotification updated to: '$message'")
                }
                _webSocketClient = client
                client.connect()
                logD("WebSocket connected successfully")
            } catch (e: Exception) {
                Log.e("TAG", "WebSocket connection error: ${e.message}", e)
            }
        }
    }

    fun sendMessage(message: String, user: String) {
        _webSocketClient?.send(message)
        logD(message)

        viewModelScope.launch(Dispatchers.IO) {
            _messageNotification.value = user

        }
        logD("$user kjfdhkjsfdhjkdsah")
    }


    fun saveUserName(userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            appPreference.setString(ConstVariables.userName, userName)
        }
    }

    fun sendFriendRequest(senderUsername: String, receiverUsername: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = FriendRequest(
                    senderUserName = senderUsername,
                    receiverUserName = receiverUsername,
                    status = "pending"
                )
                val result = apiServiceUseCase.sendFriendRequest(request)
                if (result.isSuccess) {
                    val response = result.getOrNull()
                    _friendRequestResult.value = response?.message ?: "Заявка отправлена"
                    _pendingRequests.value = _pendingRequests.value + receiverUsername
                } else {
                    _friendRequestResult.value = "Ошибка: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                Log.e("ListUserViewModel", "Error sending friend request", e)
                _friendRequestResult.value = "Ошибка сети: ${e.message}"
            }
        }
    }

    fun resetFriendRequestResult() {
        _friendRequestResult.value = null
    }

    fun acceptFriend(senderUsername: String, receiverUsername: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = AcceptFriendRequest(senderUsername, receiverUsername)
                val result = apiServiceUseCase.acceptFriend(request)
                if (result.isSuccess) {
                    _friendRequestResult.value = "Заявка принята"
                    _pendingRequests.value = _pendingRequests.value - senderUsername
                } else {
                    _friendRequestResult.value = "Ошибка: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                Log.e("ListUserViewModel", "Error accepting friend", e)
                _friendRequestResult.value = "Ошибка сети: ${e.message}"
            }
        }
    }

    fun rejectFriend(senderUsername: String, receiverUsername: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = AcceptFriendRequest(senderUsername, receiverUsername)
                val result = apiServiceUseCase.rejectFriend(request)
                if (result.isSuccess) {
                    _friendRequestResult.value = "Заявка отклонена"
                    _pendingRequests.value = _pendingRequests.value - senderUsername
                } else {
                    _friendRequestResult.value = "Ошибка: ${result.exceptionOrNull()?.message}"
                }
            } catch (e: Exception) {
                Log.e("ListUserViewModel", "Error rejecting friend", e)
                _friendRequestResult.value = "Ошибка сети: ${e.message}"
            }
        }
    }

    fun disconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            _webSocketClient?.disconnect()
        }
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
        stopPolling()
    }
}
