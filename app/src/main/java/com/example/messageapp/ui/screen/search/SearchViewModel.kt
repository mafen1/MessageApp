package com.example.messageapp.ui.screen.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.BuildConfig
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.core.logD
import com.example.messageapp.data.network.webSocket.client.ChatWebSocketClient
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.usecase.AcceptFriendRequestUseCase
import com.example.messageapp.domain.usecase.AppPreferencesUseCase
import com.example.messageapp.domain.usecase.GetFriendRequestsUseCase
import com.example.messageapp.domain.usecase.RejectFriendRequestUseCase
import com.example.messageapp.domain.usecase.SearchUsersUseCase
import com.example.messageapp.domain.usecase.SendFriendRequestUseCase
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
class SearchViewModel @Inject constructor(
    private val appPreference: AppPreferencesUseCase,
    private val searchUsersUseCase: SearchUsersUseCase,
    private val getFriendRequestsUseCase: GetFriendRequestsUseCase,
    private val sendFriendRequestUseCase: SendFriendRequestUseCase,
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase,
    private val rejectFriendRequestUseCase: RejectFriendRequestUseCase
) : ViewModel() {

    private var _foundUsers = MutableStateFlow<List<User>>(emptyList())
    var foundUser: StateFlow<List<User>> = _foundUsers

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
                    val result = getFriendRequestsUseCase(userName)
                    if (result.isSuccess) {
                        val requests = result.getOrNull() ?: emptyList()
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
                val users = searchUsersUseCase(userName)
                val filtered = users.getOrThrow().filter { it.userName != currentUserName }
                _foundUsers.value = filtered
            } catch (e: Exception) {
                Log.e("ListUserViewModel", "Error finding user by string", e)
            }
        }
    }

    fun clearSearchResults() {
        _foundUsers.value = emptyList()
    }

    fun searchUser(userName: String, currentUserName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = searchUsersUseCase(userName)
                val u = user.getOrThrow().firstOrNull()
                if (u != null && u.userName != currentUserName) {
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
                val result = sendFriendRequestUseCase(senderUsername, receiverUsername)
                if (result.isSuccess) {
                    val response = result.getOrNull()
                    _friendRequestResult.value = response ?: "Заявка отправлена"
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
                val result = acceptFriendRequestUseCase(senderUsername, receiverUsername)
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
                val result = rejectFriendRequestUseCase(senderUsername, receiverUsername)
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
