package com.example.messageapp.ui.registerScreen

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.core.logD
import com.example.messageapp.data.network.api.client.RetrofitClient.apiService
import com.example.messageapp.data.network.model.LoginRequest
import com.example.messageapp.data.network.model.LoginResponse
import com.example.messageapp.data.network.model.User
import com.example.messageapp.domain.useCase.ApiServiceUseCase
import com.example.messageapp.domain.useCase.AppPreferencesUseCase
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.OkHttpClient.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

// RegisterViewModel.kt
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val appPreference: AppPreferencesUseCase,
    private val apiServiceUseCase: ApiServiceUseCase
) : ViewModel() {

    private val _currentUser = MutableLiveData<User>()
    val currentUser: LiveData<User> = _currentUser

    private val _registrationSuccess = MutableLiveData<User?>()
    val registrationSuccess: LiveData<User?> = _registrationSuccess

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun addAccount(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Получаем Response вместо Result
                val response = apiServiceUseCase.addUser(user)

                if (response.isSuccess) {
                    val loginResponse = response.getOrNull()
                    if (loginResponse != null) {
                        appPreference.save(ConstVariables.tokenJWT, loginResponse.token)
                        withContext(Dispatchers.Main) {
                            _registrationSuccess.postValue(loginResponse.user)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            _error.postValue("Получен пустой ответ от сервера")
                        }
                    }
                } else {
                    val errorBody = response.exceptionOrNull()?.toString() ?: "Неизвестная ошибка"
                    withContext(Dispatchers.Main) {
                        _error.postValue("Ошибка регистрации: $errorBody")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _error.postValue("Ошибка сети: ${e.message}")
                }
            }
        }
    }

    fun loginAccount(loginRequest: LoginRequest) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiServiceUseCase.loginUser(loginRequest)
                if (response.isSuccess) {
                    val user = response.getOrNull()
                    user?.let {
                        appPreference.save(ConstVariables.tokenJWT, it.token ?: "")
                        _currentUser.postValue(it)
                    }
                } else {
                    _error.postValue("Неверные данные для входа")
                }
            } catch (e: Exception) {
                _error.postValue("Ошибка входа: ${e.message}")
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