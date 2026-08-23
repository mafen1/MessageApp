package com.example.messageapp.ui.screen.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.core.logD
import com.example.messageapp.domain.model.NewsPost
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.usecase.CommentPostUseCase
import com.example.messageapp.domain.usecase.GetAllUsersUseCase
import com.example.messageapp.domain.usecase.GetNewsFeedUseCase
import com.example.messageapp.domain.usecase.LikePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsFeedUseCase: GetNewsFeedUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val commentPostUseCase: CommentPostUseCase,
    private val getAllUsersUseCase: GetAllUsersUseCase
) : ViewModel() {

    private var _newsList = MutableStateFlow<List<NewsPost>>(emptyList())
    var newsList: StateFlow<List<NewsPost>> = _newsList

    private var _user = MutableStateFlow<User?>(null)
    var user: StateFlow<User?> = _user

    private var _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    fun allNews() {
        if (_isRefreshing.value) return
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.value = true
            try {
                val result = getNewsFeedUseCase()
                result.getOrNull()?.let { posts ->
                    // подтягиваем актуальные имена авторов: в БД хранится снапшот на момент поста
                    _newsList.value = applyFreshAuthorNames(posts)
                }
                logD(_newsList.value.toString())
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    /** Заменяет сохранённые имена авторов на текущие из профиля пользователя. */
    private suspend fun applyFreshAuthorNames(posts: List<NewsPost>): List<NewsPost> {
        if (posts.isEmpty()) return posts
        val users = getAllUsersUseCase().getOrNull() ?: return posts
        if (users.isEmpty()) return posts
        val currentNames = users.associate { it.userName to it.name }
        return posts.map { post ->
            currentNames[post.userNameAuthor]
                ?.takeIf { it.isNotBlank() && it != post.nameAuthor }
                ?.let { post.copy(nameAuthor = it) }
                ?: post
        }
    }

    fun saveUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            _user.value = user
        }
    }

    fun toggleLike(news: NewsPost, userName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = likePostUseCase(news.id, userName)
            result.getOrNull()?.let(::replaceNews)
        }
    }

    fun addComment(news: NewsPost, userName: String, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = commentPostUseCase(news.id, userName, text)
            result.getOrNull()?.let(::replaceNews)
        }
    }

    private fun replaceNews(updated: NewsPost) {
        _newsList.value = _newsList.value.map { news ->
            if (news.id == updated.id) updated else news
        }
    }
}
