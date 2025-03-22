package com.example.messageapp.ui.registerScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.data.model.User
import com.example.messageapp.data.network.api.client.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterViewModel: ViewModel() {

    fun addAccount(user: String){
        viewModelScope.launch(Dispatchers.IO) {
            RetrofitClient.apiService.addUser(user)
        }
    }
}