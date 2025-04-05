package com.example.messageapp.ui.privateScreen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController

import androidx.navigation.fragment.findNavController
import com.example.messageapp.R
import com.example.messageapp.data.network.api.client.RetrofitClient
import com.example.messageapp.data.network.model.Token
import com.example.messageapp.data.network.model.User
import com.example.messageapp.store.SharedPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PrivateViewModel : ViewModel() {

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

                } catch (e: Exception) {
                    Log.e("TAGG", "Error fetching user: ${e.message}")
                }
            } else {

            }
        }
    }
}