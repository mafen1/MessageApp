package com.example.messageapp.ui.chatList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.data.network.api.client.RetrofitClient
import com.example.messageapp.data.network.model.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatListViewModel : ViewModel() {

    private var _listUser = MutableLiveData<MutableList<UserResponse>>()
    var listUser: LiveData<MutableList<UserResponse>> = _listUser


    fun allUser() {
        viewModelScope.launch(Dispatchers.IO) {
            _listUser.postValue(RetrofitClient.apiService.allUser())
        }
    }
}