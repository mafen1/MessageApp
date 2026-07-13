package com.example.messageapp.ui.screen.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.core.TokenStorage
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.usecase.AppPreferencesUseCase
import com.example.messageapp.domain.usecase.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val appPreferencesUseCase: AppPreferencesUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _saveResult = MutableStateFlow<String?>(null)
    val saveResult: StateFlow<String?> = _saveResult

    fun loadUser() {
        viewModelScope.launch(Dispatchers.IO) {
            val name = appPreferencesUseCase.getString(ConstVariables.nameUser).first()
            val username = appPreferencesUseCase.getString(ConstVariables.userName).first()
            val token = appPreferencesUseCase.getString(ConstVariables.tokenJWT).first()
            TokenStorage.setToken(token)
            _user.value = User(name = name, userName = username, token = token)
        }
    }

    fun updateProfile(name: String, password: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            val username = _user.value?.userName ?: return@launch
            val result = updateProfileUseCase(username, name, password)
            val updated = result.getOrNull()
            if (updated != null) {
                appPreferencesUseCase.setString(ConstVariables.nameUser, updated.name)
                appPreferencesUseCase.setString(ConstVariables.userName, updated.userName)
                _user.value = updated.copy(token = _user.value?.token ?: "")
                _saveResult.value = "Профиль сохранён"
            } else {
                _saveResult.value = result.exceptionOrNull()?.message ?: "Ошибка сохранения"
            }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            appPreferencesUseCase.setString(ConstVariables.tokenJWT, "")
            appPreferencesUseCase.setString(ConstVariables.userName, "")
            appPreferencesUseCase.setString(ConstVariables.nameUser, "")
            TokenStorage.clear()
        }
    }

    fun resetSaveResult() {
        _saveResult.value = null
    }
}
