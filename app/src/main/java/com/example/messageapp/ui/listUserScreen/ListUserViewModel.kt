package com.example.messageapp.ui.listUserScreen

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.data.network.api.client.RetrofitClient
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.data.network.webSocket.client.ChatWebSocketClient
import com.example.messageapp.store.SharedPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URI
import javax.inject.Inject

@HiltViewModel
class ListUserViewModel @Inject constructor() : ViewModel() {

    private var _foundUser = MutableLiveData<MutableList<UserResponse>>()
    var foundUser: LiveData<MutableList<UserResponse>> = _foundUser

    private val _messageNotification = MutableLiveData<String>()
    var messageNotification: LiveData<String> = _messageNotification

    private var _webSocketClient: ChatWebSocketClient? = null


    fun searchUser(userName: UserRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = RetrofitClient.apiService.findUserByName(userName)
                _foundUser.postValue(mutableListOf(user))
                Log.d("TAG", user.toString())
            } catch (e: Exception) {
                Log.d("TAG", e.message.toString())
            }
        }
    }

    fun connectWebSocket(userName: String) {
        val serverUri = URI("ws://10.0.2.2:8081/friendMessage/$userName")
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

    fun saveUserName(context: Context, userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            SharedPreference(context).save("username", userName)
        }
    }

    fun findUserByStr(userName: String){
        viewModelScope.launch(Dispatchers.IO) {
            val users = RetrofitClient.apiService.findUserByStr(UserRequest(userName))
            _foundUser.postValue(users.toMutableList())
        }
    }
}