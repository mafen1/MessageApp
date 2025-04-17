package com.example.messageapp.ui.chatList

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.data.network.api.client.RetrofitClient
import com.example.messageapp.data.network.model.UserResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatListViewModel: ViewModel() {

    var listUser = MutableLiveData<MutableList<UserResponse>>()

    fun findUserByUserName(){
        viewModelScope.launch(Dispatchers.IO){

        }
    }

    fun allUser(){
        viewModelScope.launch(Dispatchers.IO) {
            listUser.postValue(RetrofitClient.apiService.allUser())
        }
    }
}