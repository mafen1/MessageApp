package com.example.messageapp.ui.chatListScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.UiState
import com.example.messageapp.core.logD
import com.example.messageapp.data.network.model.User
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.domain.useCase.ApiServiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val apiServiceUseCase: ApiServiceUseCase
) : ViewModel() {

    data class ChatListState(
        val users: List<UserResponse>,
        val currentUser: User
    )

    private val _uiState = MutableStateFlow<UiState<ChatListState>>(UiState.Loading)
    val uiState: StateFlow<UiState<ChatListState>> = _uiState

    fun loadChatList(userName: String, userFullName: String) {
        logD("loadChatList called for: $userName")
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                // Получаем список друзей
                val friendsResult = apiServiceUseCase.getFriends(userName)
                if (friendsResult.isFailure) {
                    throw friendsResult.exceptionOrNull() ?: Exception("Unknown error")
                }
                val friendsResponse = friendsResult.getOrNull()

                logD("getFriends result: isSuccess=${friendsResult.isSuccess}")
                logD("getFriends response: $friendsResponse")

                val friends = friendsResponse?.friends ?: emptyList()
                logD("friends list: $friends (size: ${friends.size})")

                val currentUser = User(name = userFullName, userName = userName)

                if (friends.isEmpty()) {
                    logD("No friends found for user: $userName")
                    _uiState.value = UiState.Success(ChatListState(emptyList(), currentUser))
                    return@launch
                }

                // Получаем всех пользователей
                val allUsersResult = apiServiceUseCase.allUser()
                if (allUsersResult.isFailure) {
                    throw allUsersResult.exceptionOrNull() ?: Exception("Unknown error")
                }
                val allUsers = allUsersResult.getOrNull() ?: emptyList()
                logD("allUsers loaded: ${allUsers.size} items")

                // Фильтруем по списку друзей
                val friendsList = allUsers.filter { user ->
                    friends.contains(user.username)
                }

                logD("filtered friendsList: ${friendsList.size} items: $friendsList")
                _uiState.value = UiState.Success(ChatListState(friendsList, currentUser))
            } catch (e: Exception) {
                Log.e("ChatListVM", "Error loading chat list: ${e.message}", e)
                _uiState.value = UiState.Error("Ошибка загрузки чатов: ${e.message}")
            }
        }
    }
}
