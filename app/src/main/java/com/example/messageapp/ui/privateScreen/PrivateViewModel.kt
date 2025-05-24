package com.example.messageapp.ui.privateScreen

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.core.logD
import com.example.messageapp.data.network.model.Token
import com.example.messageapp.data.network.model.User
import com.example.messageapp.domain.useCase.ApiServiceUseCase
import com.example.messageapp.domain.useCase.AppPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivateViewModel @Inject constructor(
    private val appPreference: AppPreferencesUseCase,
    private val apiServiceUseCase: ApiServiceUseCase
) : ViewModel() {

    private val _userResponse = MutableLiveData<User>()
    var userResponse = _userResponse

    fun findUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = appPreference.getValueString(ConstVariables.tokenJWT)
            Log.d("TAG", token.toString())
            if (token!!.isNotEmpty()) {
                try {
                    val user = apiServiceUseCase.findUser(Token(token))

                    logD("Received user: $user")

                    _userResponse.postValue(user.getOrThrow())
                    appPreference.save(ConstVariables.userName, user.getOrThrow().userName)

                } catch (e: Exception) {
                    Log.e("TAGG", "Error fetching user: ${e.message}")
                }
            } else {
                throw Exception("Токена нет")
            }
        }
    }


}