package com.example.messageapp.ui.newsListScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.logD
import com.example.messageapp.data.network.model.CommentRequest
import com.example.messageapp.data.network.model.LikeRequest
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

    private var _newsList = MutableStateFlow<List<NewsResponse>>(emptyList())
    var newsList: StateFlow<List<NewsResponse>> = _newsList

    private var _user = MutableStateFlow<User?>(null)
    var user: StateFlow<User?> = _user


    fun allNews(){
        viewModelScope.launch(Dispatchers.IO) {
            _newsList.value = apiServiceUseCase.allNews()
            logD(newsList.value.toString())
        }
    }

    fun saveUser(user: User){
        viewModelScope.launch(Dispatchers.IO) {
            _user.value = (user)
        }
    }

    fun toggleLike(news: NewsResponse, userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = apiServiceUseCase.toggleLike(LikeRequest(news.id, userName))
            result.getOrNull()?.let(::replaceNews)
        }
    }

    fun addComment(news: NewsResponse, userName: String, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = apiServiceUseCase.addComment(CommentRequest(news.id, userName, text))
            result.getOrNull()?.let(::replaceNews)
        }
    }

    private fun replaceNews(updated: NewsResponse) {
        _newsList.value = _newsList.value.map { news ->
            if (news.id == updated.id) updated else news
        }
    }

}
