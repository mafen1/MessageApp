package com.example.messageapp.ui.chatListScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.domain.useCase.ApiServiceUseCase
import com.example.messageapp.domain.useCase.AppPreferencesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val apiServiceUseCase: ApiServiceUseCase
) : ViewModel() {

    private var _listUsers = MutableStateFlow<MutableList<UserResponse>>(mutableListOf())
    var listUsers: StateFlow<MutableList<UserResponse>> = _listUsers


    fun allUser() {
        viewModelScope.launch(Dispatchers.IO) {
            _listUsers.value = (apiServiceUseCase.allUser().getOrThrow())

        }
    }

    fun filteredUsers(
        currentUserName: String,
        currentUserFullName: String
    ) {
            for (user in _listUsers.value) {
                if (currentUserName == user.username && currentUserFullName == user.name){
                    _listUsers.value.remove(UserResponse(user.username,user.name))
                }
            }

    }

//    return _listUsers.map { users ->
//        users.filterNot { user ->
//            user.username == currentUserName && user.name == currentUserFullName
//        }
//    }

//    fun deleteCurrentUser(currentUserName: String, currentName: String){
//        _listUsers.value = _listUsers.value?.filterNot {
//            it.username == currentUserName && it.name == currentName
//        } as MutableList<UserResponse>?
//    }

//    fun findUser() {
//        viewModelScope.launch(Dispatchers.IO) {
//            val token = appPreference.getValueString(ConstVariables.tokenJWT)
//
//            if (token!!.isNotEmpty()) {
//                try {
//                    val user = apiServiceUseCase.findUser(Token(token))
//                    Log.d("TAGG", "Received user: $user")
//                    _userResponse.value = user.getOrThrow()
//
//
//                } catch (e: Exception) {
//                    Log.e("TAGG", "Error fetching user: ${e.message}")
//                }
//            } else {
//                throw Exception("Токена нет")
//            }
//        }
//    }
}