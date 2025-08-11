package com.example.messageapp.ui.listUserScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.data.network.webSocket.client.ChatWebSocketClient
import com.example.messageapp.domain.useCase.ApiServiceUseCase
import com.example.messageapp.domain.useCase.AppPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URI
import javax.inject.Inject

@HiltViewModel
class ListUserViewModel @Inject constructor(
    private val appPreference: AppPreferencesUseCase,
    private val apiServiceUseCase: ApiServiceUseCase
) : ViewModel() {

    private var _foundUser = MutableLiveData<MutableList<UserResponse>>()
    var foundUser: LiveData<MutableList<UserResponse>> = _foundUser

    private val _messageNotification = MutableLiveData<String>()
    var messageNotification: LiveData<String> = _messageNotification

    private var _webSocketClient: ChatWebSocketClient? = null


    fun searchUser(userName: UserRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = apiServiceUseCase.findUserByName(userName)
                _foundUser.postValue(mutableListOf(user.getOrThrow()))
                Log.d("TAG", user.toString())
            } catch (e: Exception) {
                Log.d("TAG", e.message.toString())
            }
        }
    }

    fun connectWebSocket(userName: String) {
        val serverUri = URI("${ConstVariables.wsUrl}/friendMessage/$userName")
        // проверка есть ли такое же соединение
//        _webSocketClient?.disconnect()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _webSocketClient = ChatWebSocketClient(serverUri) { message ->
                    _messageNotification.postValue(message)
                }
                _webSocketClient!!.connect()
            } catch (e: Exception) {
                Log.d("TAG", e.message.toString())
            }
        }
    }

    fun sendMessage(message: String) {
        _webSocketClient?.send(message)
    }

    fun saveUserName( userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            appPreference.save("username", userName)
        }
    }

    fun findUserByStr(userName: String){
        viewModelScope.launch(Dispatchers.IO) {
            val users = apiServiceUseCase.findUserByStr(UserRequest(userName))
            _foundUser.postValue(users.getOrThrow().toMutableList())
        }
    }
    // todo onDestroy

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