package com.example.messageapp.ui.screen.welcome

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.core.TokenStorage
import com.example.messageapp.core.logD
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.usecase.AppPreferencesUseCase
import com.example.messageapp.domain.usecase.GetCurrentUserUseCase
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
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    fun loginUser() {
        viewModelScope.launch(Dispatchers.IO) {
            _token.value = appPreference.getString(ConstVariables.tokenJWT).first()
            val currentToken = _token.value

            if (!currentToken.isNullOrEmpty()) {
                TokenStorage.setToken(currentToken)
                try {
                    val user = getCurrentUserUseCase()

                    logD("Received user: $user")

                    _user.value = user.getOrThrow()
                    appPreference.setString(ConstVariables.userName, user.getOrNull()?.userName.orEmpty())
                    appPreference.setString(ConstVariables.nameUser, user.getOrNull()?.name.orEmpty())
                } catch (e: Exception) {
                    Log.e("TAGG", "Error fetching user: ${e.message}")
                    _user.value = null
                } finally {
                    _isLoading.value = false
                }
            } else {
                _user.value = null
                _isLoading.value = false
                logD("USER NULL")
            }
        }
    }
}
