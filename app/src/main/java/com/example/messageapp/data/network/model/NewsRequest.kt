package com.example.messageapp.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NewsRequest(
    var userNameAuthor: String,
    var nameAuthor: String,
    var date: String,
    val countLike: Int,
    val countComment: Int,
    var avatarAuthor: String?,
    val description: String,
    val comment: List<String>
)
