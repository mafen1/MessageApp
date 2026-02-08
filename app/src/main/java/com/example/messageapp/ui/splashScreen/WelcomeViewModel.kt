package com.example.messageapp.ui.splashScreen

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val appPreference: AppPreferencesUseCase,
    private val apiServiceUseCase: ApiServiceUseCase
): ViewModel() {

    private val _user = MutableLiveData<User?>()
    var user = _user

    private var _token: MutableStateFlow<String?> = MutableStateFlow(null)
    var token: StateFlow<String?> = _token

    private var _nameRequestFriends: MutableStateFlow<String?> = MutableStateFlow(null)
    var nameRequestFriends: StateFlow<String?> = _nameRequestFriends


    fun loginUser() {
        viewModelScope.launch(Dispatchers.IO) {

            _token.value = appPreference.getString(ConstVariables.tokenJWT).first()

            if (_token.value?.isNotEmpty() == true) {
                try {
                    val user = apiServiceUseCase.fetchUser(Token(_token.value!!))

                    logD("Received user: $user")

                    _user.postValue(user.getOrThrow())
                    appPreference.save(ConstVariables.userName, user.getOrNull()?.userName)
                    appPreference.save(ConstVariables.nameUser, user.getOrNull()?.name)


                } catch (e: Exception) {
                    Log.e("TAGG", "Error fetching user: ${e.message}")
                }
            } else {
                _user.postValue(null)
                logD("USER NULL")
            }
        }
    }






}