package com.example.messageapp.ui.screen.chatlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.UiState
import com.example.messageapp.core.logD
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.usecase.GetAllUsersUseCase
import com.example.messageapp.domain.usecase.GetFriendsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val getFriendsUseCase: GetFriendsUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase
) : ViewModel() {

    data class ChatListState(
        val users: List<User>,
        val currentUser: User
    )

    private val _uiState = MutableStateFlow<UiState<ChatListState>>(UiState.Loading)
    val uiState: StateFlow<UiState<ChatListState>> = _uiState

    fun loadChatList(userName: String, userFullName: String) {
        logD("loadChatList called for: $userName")
        _uiState.value = UiState.Loading

        viewModelScope.launch {
            try {
                val friendsResult = getFriendsUseCase(userName)
                if (friendsResult.isFailure) {
                    throw friendsResult.exceptionOrNull() ?: Exception("Unknown error")
                }
                val friends = friendsResult.getOrNull() ?: emptyList()
                logD("friends list: $friends (size: ${friends.size})")

                val currentUser = User(name = userFullName, userName = userName)

                if (friends.isEmpty()) {
                    logD("No friends found for user: $userName")
                    _uiState.value = UiState.Success(ChatListState(emptyList(), currentUser))
                    return@launch
                }

                val allUsersResult = getAllUsersUseCase()
                if (allUsersResult.isFailure) {
                    throw allUsersResult.exceptionOrNull() ?: Exception("Unknown error")
                }
                val allUsers = allUsersResult.getOrNull() ?: emptyList()
                logD("allUsers loaded: ${allUsers.size} items")

                val friendsList = allUsers.filter { user ->
                    friends.contains(user.userName)
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
