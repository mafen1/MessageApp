package com.example.messageapp.ui.listUserScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.data.network.api.client.RetrofitClient
import com.example.messageapp.data.network.model.UserRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListUserViewModel: ViewModel() {

    fun searchUser(userName: UserRequest){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val user = RetrofitClient.apiService.findUserByName(userName)
                Log.d("TAG", user.toString())
            }catch (e: Exception){
                Log.d("TAG", e.message.toString())
            }
        }
    }
}