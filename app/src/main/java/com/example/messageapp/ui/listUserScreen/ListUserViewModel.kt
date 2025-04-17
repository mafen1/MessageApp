package com.example.messageapp.ui.listUserScreen

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.data.network.api.client.RetrofitClient
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.data.network.webSocket.client.ChatWebSocketClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URI

class ListUserViewModel : ViewModel() {

    var foundUser = MutableLiveData<MutableList<UserResponse>>()
    val messageNotification = MutableLiveData<String>()
    var webSocketClient :ChatWebSocketClient? = null

    fun searchUser(userName: UserRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = RetrofitClient.apiService.findUserByName(userName)
                foundUser.postValue(mutableListOf(user))
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
                webSocketClient = ChatWebSocketClient(serverUri) { message ->
                    messageNotification.postValue(message)
                }
                webSocketClient!!.connect()
            } catch (e: Exception) {
                Log.d("TAG", e.message.toString())
            }
        }
    }
}