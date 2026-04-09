package com.example.messageapp.ui.chatListScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.logD
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.domain.useCase.ApiServiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val apiServiceUseCase: ApiServiceUseCase
) : ViewModel() {

    private var _displayUsers = MutableStateFlow<List<UserResponse>>(emptyList())
    var displayUsers: StateFlow<List<UserResponse>> = _displayUsers

    private var _isLoading = MutableStateFlow(true)
    var isLoading: StateFlow<Boolean> = _isLoading

    private var currentUserName = ""
    private var currentUserFullName = ""

    fun loadChatList(userName: String, userFullName: String) {
        currentUserName = userName
        currentUserFullName = userFullName
        logD("loadChatList called for: $userName")

        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Получаем список друзей
                val friendsResult = apiServiceUseCase.getFriends(userName)
                val friendsResponse = friendsResult.getOrNull()
                
                logD("getFriends result: isSuccess=${friendsResult.isSuccess}")
                logD("getFriends response: $friendsResponse")
                
                val friends = friendsResponse?.friends ?: emptyList()
                logD("friends list: $friends (size: ${friends.size})")
                
                if (friends.isEmpty()) {
                    logD("No friends found for user: $userName")
                    _displayUsers.value = emptyList()
                    _isLoading.value = false
                    return@launch
                }

                // Получаем всех пользователей
                val allUsersResult = apiServiceUseCase.allUser()
                val allUsers = allUsersResult.getOrNull() ?: emptyList()
                logD("allUsers loaded: ${allUsers.size} items")

                // Фильтруем по списку друзей
                val friendsList = allUsers.filter { user ->
                    friends.contains(user.username)
                }
                
                logD("filtered friendsList: ${friendsList.size} items: $friendsList")
                _displayUsers.value = friendsList
            } catch (e: Exception) {
                Log.e("ChatListVM", "Error loading chat list: ${e.message}", e)
                e.printStackTrace()
                _displayUsers.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}