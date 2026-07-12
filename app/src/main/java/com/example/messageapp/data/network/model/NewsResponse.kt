package com.example.messageapp.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NewsResponse(
    val id: Int,
    var userNameAuthor: String,
    var nameAuthor: String,
    var date: String,
    val countLike: Int,
    val countComment: Int,
    var avatarAuthor: String?,
    val description: String,
    val comment: List<String>? = emptyList(),
    val newsImage: String? = null,
    val likedUsers: List<String>? = emptyList()
)
