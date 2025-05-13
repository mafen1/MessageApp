package com.example.messageapp.ui.chatListScreen

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.data.network.api.client.RetrofitClient
import com.example.messageapp.data.network.model.Token
import com.example.messageapp.data.network.model.User
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.store.SharedPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor() : ViewModel() {

    private var _listUser = MutableLiveData<MutableList<UserResponse>>()
    var listUser: LiveData<MutableList<UserResponse>> = _listUser

    private var _userResponse = MutableLiveData<User>()
    var userResponse: LiveData<User> = _userResponse


    fun allUser() {
        viewModelScope.launch(Dispatchers.IO) {
            _listUser.postValue(RetrofitClient.apiService.allUser())
        }

    }

    fun findUser(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val token = SharedPreference(context).getValueString("tokenJWT")
            Log.d("TAG", token.toString())
            if (token!!.isNotEmpty()) {
                try {
                    val user = RetrofitClient.apiService.findUser(Token(token))
                    Log.d("TAGG", "Received user: $user")
                    _userResponse.postValue(user)

                } catch (e: Exception) {
                    Log.e("TAGG", "Error fetching user: ${e.message}")
                }
            } else {
                throw Exception("Токена нет")
            }
        }
    }
}