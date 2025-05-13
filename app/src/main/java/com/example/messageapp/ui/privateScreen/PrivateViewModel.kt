package com.example.messageapp.ui.privateScreen

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.data.network.model.Token
import com.example.messageapp.data.network.model.User
import com.example.messageapp.domain.useCase.ApiServiceUseCase
import com.example.messageapp.store.AppPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivateViewModel @Inject constructor(
    // todo переделать в UseCase
    private val appPreference: AppPreference,
    private val apiServiceUseCase: ApiServiceUseCase
) : ViewModel() {

    //todo переделать
    var userResponse = MutableLiveData<User>()

    fun findUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val token = appPreference.getValueString(ConstVariables.tokenJWT)
            Log.d("TAG", token.toString())
            if (token!!.isNotEmpty()) {
                try {
                    val user = apiServiceUseCase.findUser(Token(token))
//                    val user = RetrofitClient.apiService.findUser(Token(token))
                    Log.d("TAGG", "Received user: $user")
                    userResponse.postValue(user.getOrThrow())
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