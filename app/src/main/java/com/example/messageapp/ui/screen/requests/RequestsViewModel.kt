package com.example.messageapp.ui.screen.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.domain.model.FriendRequest
import com.example.messageapp.domain.usecase.AcceptFriendRequestUseCase
import com.example.messageapp.domain.usecase.GetFriendRequestsUseCase
import com.example.messageapp.domain.usecase.RejectFriendRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequestsViewModel @Inject constructor(
    private val getFriendRequestsUseCase: GetFriendRequestsUseCase,
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase,
    private val rejectFriendRequestUseCase: RejectFriendRequestUseCase
) : ViewModel() {

    data class UiState(
        val loading: Boolean = false,
        val requests: List<FriendRequest> = emptyList(),
        val info: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private var currentUserName: String = ""

    fun load(userName: String) {
        currentUserName = userName
        _uiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            val result = getFriendRequestsUseCase(userName)
            result.fold(
                onSuccess = { requests ->
                    _uiState.update { it.copy(loading = false, requests = requests, info = null) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(loading = false, requests = emptyList(), info = "Не удалось загрузить заявки: ${e.message}")
                    }
                }
            )
        }
    }

    fun accept(senderUserName: String) = respond(senderUserName, accept = true)

    fun reject(senderUserName: String) = respond(senderUserName, accept = false)

    private fun respond(senderUserName: String, accept: Boolean) {
        viewModelScope.launch {
            val result = if (accept) {
                acceptFriendRequestUseCase(senderUserName, currentUserName)
            } else {
                rejectFriendRequestUseCase(senderUserName, currentUserName)
            }
            _uiState.update {
                it.copy(info = result.getOrNull() ?: "Ошибка: ${result.exceptionOrNull()?.message}")
            }
            // обновляем список после действия
            load(currentUserName)
        }
    }

    fun resetInfo() {
        _uiState.update { it.copy(info = null) }
    }
}
