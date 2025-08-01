package com.example.messageapp.ui.chatListScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.data.network.model.Token
import com.example.messageapp.data.network.model.User
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.domain.useCase.ApiServiceUseCase
import com.example.messageapp.domain.useCase.AppPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val appPreference: AppPreferencesUseCase,
    private val apiServiceUseCase: ApiServiceUseCase
) : ViewModel() {

    private var _listUser = MutableLiveData<MutableList<UserResponse>>()
    var listUser: LiveData<MutableList<UserResponse>> = _listUser

    private var _userResponse = MutableLiveData<User>()
    var userResponse: LiveData<User> = _userResponse


    fun allUser() {
        viewModelScope.launch(Dispatchers.IO) {
            _listUser.postValue(apiServiceUseCase.allUser().getOrThrow())
        }
    }

    fun findUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = appPreference.getValueString(ConstVariables.tokenJWT)

            if (token!!.isNotEmpty()) {
                try {
                    val user = apiServiceUseCase.findUser(Token(token))
                    Log.d("TAGG", "Received user: $user")
                    _userResponse.value = user.getOrThrow()


                } catch (e: Exception) {
                    Log.e("TAGG", "Error fetching user: ${e.message}")
                }
            } else {
                throw Exception("Токена нет")
            }
        }
    }
}