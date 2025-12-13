package com.example.messageapp.ui.newsListScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.data.network.model.NewsRequest
import com.example.messageapp.data.network.model.NewsResponse
import com.example.messageapp.data.network.model.User
import com.example.messageapp.domain.useCase.ApiServiceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NewsViewModel @Inject constructor(
   private val apiServiceUseCase: ApiServiceUseCase
) : ViewModel() {

    private var _newsList = MutableLiveData<MutableList<NewsResponse>>()
    var newsList: LiveData<MutableList<NewsResponse>> = _newsList

    private var _user = MutableLiveData<User>()
    var user: LiveData<User> = _user


    fun allNews(){
        viewModelScope.launch(Dispatchers.IO) {
            _newsList.postValue(apiServiceUseCase.allNews().toMutableList())
        }
    }

    fun saveUser(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            _user.postValue(user)
        }
    }

}