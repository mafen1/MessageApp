package com.example.messageapp.ui.registerScreen

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.data.network.api.client.RetrofitClient
import com.example.messageapp.data.network.model.LoginRequest
import com.example.messageapp.data.network.model.User
import com.example.messageapp.store.SharedPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(

) : ViewModel() {

    private var _foundUser: MutableLiveData<User> = MutableLiveData()
    var foundUser: MutableLiveData<User> = _foundUser

    fun addAccount(user: User, context: Context) {
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

    fun loginAccount(loginRequest: LoginRequest){
        viewModelScope.launch(Dispatchers.IO) {
            val user = RetrofitClient.apiService.loginUser(
                loginRequest
            )
            _foundUser.postValue(user)
        }
    }



}