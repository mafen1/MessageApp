package com.example.messageapp.ui.newsListScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.logD
import com.example.messageapp.data.network.model.NewsRequest
import com.example.messageapp.data.network.model.NewsResponse
import com.example.messageapp.data.network.model.User
import com.example.messageapp.domain.useCase.ApiServiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewsViewModel @Inject constructor(
   private val apiServiceUseCase: ApiServiceUseCase
) : ViewModel() {

    private var _newsList = MutableStateFlow<MutableList<NewsResponse>>(mutableListOf())
    var newsList: StateFlow<MutableList<NewsResponse>> = _newsList

    private var _user = MutableStateFlow<User?>(null)
    var user: StateFlow<User?> = _user


    fun allNews(){
        viewModelScope.launch(Dispatchers.IO) {
            _newsList.value = apiServiceUseCase.allNews().toMutableList()
            logD(newsList.value.toString())
        }
    }

    fun saveUser(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            _user.value = (user)
        }
    }

}