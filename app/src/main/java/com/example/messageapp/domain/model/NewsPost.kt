package com.example.messageapp.domain.model

data class NewsPost(
    val id: Int = 0,
    val userNameAuthor: String,
    val nameAuthor: String,
    val date: String,
    val countLike: Int = 0,
    val countComment: Int = 0,
    val avatarAuthor: String? = null,
    val description: String,
    val comments: List<String> = emptyList(),
    val imageUrl: String? = null,
    val likedUsers: List<String> = emptyList()
)
