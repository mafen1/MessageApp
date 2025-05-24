package com.example.messageapp.ui.registerScreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.core.logD
import com.example.messageapp.data.network.api.client.RetrofitClient
import com.example.messageapp.data.network.model.LoginRequest
import com.example.messageapp.data.network.model.User
import com.example.messageapp.domain.useCase.ApiServiceUseCase
import com.example.messageapp.domain.useCase.AppPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

// todo apiServiceUseCase
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val appPreference: AppPreferencesUseCase,
    private val apiServiceUseCase: ApiServiceUseCase
) : ViewModel() {

    private var _foundUser: MutableLiveData<User> = MutableLiveData()
    var foundUser: MutableLiveData<User> = _foundUser

    private var _messageUser: MutableLiveData<String> = MutableLiveData()
    var messageUser: MutableLiveData<String> = _messageUser

    fun addAccount(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = apiServiceUseCase.addUser(user)
            try {
                appPreference.save(ConstVariables.tokenJWT,  response.body()?.token!!)

                logD("${appPreference.getValueString(ConstVariables.tokenJWT)}")
            } catch (e: Exception) {
                logD(e.toString())
            }
        }
    }

    fun loginAccount(loginRequest: LoginRequest){
        viewModelScope.launch(Dispatchers.IO) {
            val user = RetrofitClient.apiService.loginUser(
                loginRequest
            )
            _foundUser.postValue(user)
            appPreference.save(ConstVariables.tokenJWT, user.token!!)
        }
    }




}