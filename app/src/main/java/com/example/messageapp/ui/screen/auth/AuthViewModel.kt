package com.example.messageapp.ui.screen.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.core.TokenStorage
import com.example.messageapp.core.logD
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.model.UserCredentials
import com.example.messageapp.domain.usecase.AppPreferencesUseCase
import com.example.messageapp.domain.usecase.LoginUseCase
import com.example.messageapp.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appPreference: AppPreferencesUseCase,
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _registrationSuccess = MutableStateFlow<User?>(null)
    val registrationSuccess: StateFlow<User?> = _registrationSuccess

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun addAccount(credentials: UserCredentials) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = registerUseCase(credentials)

                if (response.isSuccess) {
                    val loginResponse = response.getOrNull()

                    if (loginResponse != null) {
                        appPreference.setString(ConstVariables.tokenJWT, loginResponse.token)
                        TokenStorage.setToken(loginResponse.token)
                        appPreference.setString(ConstVariables.userName, loginResponse.user.userName)
                        appPreference.setString(ConstVariables.nameUser, loginResponse.user.name)
                        _registrationSuccess.value = loginResponse.user
                    } else {
                        _error.value = ("Получен пустой ответ от сервера")
                    }
                } else {
                    _error.value = ("Ошибка регистрации: ${response.exceptionOrNull()}")
                }
            } catch (e: Exception) {
                _error.value = ("Ошибка сети: ${e.message}")
            }
        }
    }

    fun loginAccount(credentials: UserCredentials) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = loginUseCase(credentials)
                if (response.isSuccess) {
                    val loginResponse = response.getOrNull()
                    if (loginResponse != null) {
                        appPreference.setString(ConstVariables.tokenJWT, loginResponse.token)
                        TokenStorage.setToken(loginResponse.token)
                        appPreference.setString(ConstVariables.userName, loginResponse.user.userName)
                        appPreference.setString(ConstVariables.nameUser, loginResponse.user.name)
                        _currentUser.value = loginResponse.user
                    } else {
                        _error.value = "Пустой ответ сервера"
                    }
                } else {
                    _error.value = ("Неверные данные для входа")
                }
            } catch (e: Exception) {
                _error.value = ("Ошибка входа: ${e.message}")
            }
        }
    }

    fun resetRegistrationState() {
        _registrationSuccess.value = null
    }

    fun resetError() {
        _error.value = null
    }
}
