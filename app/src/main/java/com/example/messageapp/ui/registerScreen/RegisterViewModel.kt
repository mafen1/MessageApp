package com.example.messageapp.ui.registerScreen

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.data.network.model.User
import com.example.messageapp.data.network.api.client.RetrofitClient
import com.example.messageapp.store.SharedPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// todo friend сделать приватным

class RegisterViewModel: ViewModel() {
     var friend = MutableLiveData<List<String>>()

     var tokenJWT = MutableLiveData<String>()

    fun addAccount(user: User, context: Context){
        viewModelScope.launch(Dispatchers.IO) {
            val response = RetrofitClient.apiService.addUser(user)
            if (response.isSuccessful) {
                SharedPreference(context).save("tokenJWT", response.body()?.token!!)
                Log.d("TAG", "Token: ${SharedPreference(context).getValueString("tokenJWT")}")
            } else {
                Log.e("TAG", "Ошибка: ${response.errorBody()?.string()}")
            }
        }
    }

//    fun listFriends(){
//        viewModelScope.launch(Dispatchers.IO) {
//            friend.value = RetrofitClient.apiService.friends()
//        }
//    }
}