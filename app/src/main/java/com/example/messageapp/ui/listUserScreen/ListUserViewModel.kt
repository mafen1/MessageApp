package com.example.messageapp.ui.listUserScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.core.logD
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.data.network.webSocket.client.ChatWebSocketClient
import com.example.messageapp.domain.useCase.ApiServiceUseCase
import com.example.messageapp.domain.useCase.AppPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.URI
import javax.inject.Inject

@HiltViewModel
class ListUserViewModel @Inject constructor(
    private val appPreference: AppPreferencesUseCase,
    private val apiServiceUseCase: ApiServiceUseCase
) : ViewModel() {

    private var _foundUsers = MutableStateFlow<MutableList<UserResponse>>(mutableListOf())
    var foundUser: StateFlow<MutableList<UserResponse>> = _foundUsers

    private val _messageNotification = MutableStateFlow("")
    var messageNotification: StateFlow<String> = _messageNotification

    private var _webSocketClient: ChatWebSocketClient? = null

    fun searchUser(userName: UserRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = apiServiceUseCase.findUserByName(userName)
                _foundUsers.value = (mutableListOf(user.getOrThrow()))
            } catch (e: Exception) {
                Log.e("ListUserViewModel", "Error searching user", e)
            }
        }
    }

    fun connectWebSocket(userName: String) {
        val serverUri = URI("${ConstVariables.wsUrl}/friendMessage/$userName")
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _webSocketClient = ChatWebSocketClient(serverUri) { message ->
                    _messageNotification.value = message
                }
                _webSocketClient!!.connect()
            } catch (e: Exception) {
                Log.d("TAG", "WebSocket connection error: ${e.message}", e)
            }
        }
    }

    fun sendMessage(message: String, user: String) {
        _webSocketClient?.send(message)
        logD(message)

        // todo ?
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

    fun findUserByStr(userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val users = apiServiceUseCase.findUserByStr(UserRequest(userName))
                _foundUsers.value = users.getOrThrow().toMutableList()
            } catch (e: Exception) {
                Log.e("ListUserViewModel", "Error finding user by string", e)
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
    }
}