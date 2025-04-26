package com.example.messageapp.ui.privateScreen

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.data.network.api.client.RetrofitClient
import com.example.messageapp.data.network.model.Token
import com.example.messageapp.data.network.model.User
import com.example.messageapp.store.SharedPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PrivateViewModel : ViewModel() {

    //todo переделать
    var userResponse = MutableLiveData<User>()

    fun findUser(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val token = SharedPreference(context).getValueString("tokenJWT")
            Log.d("TAG", token.toString())
            if (token!!.isNotEmpty()) {
                try {
                    val user = RetrofitClient.apiService.findUser(Token(token))
                    Log.d("TAGG", "Received user: $user")
                    userResponse.postValue(user)
                    SharedPreference(context).save("username", user.userName)

                } catch (e: Exception) {
                    Log.e("TAGG", "Error fetching user: ${e.message}")
                }
            } else {
                throw Exception("Токена нет")
            }
        }
    }


}