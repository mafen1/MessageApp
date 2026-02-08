package com.example.messageapp.data.network.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import okhttp3.MultipartBody

@Serializable
data class NewsResponse(
    var userNameAuthor: String,
    var nameAuthor: String,
    var date: String,
    val countLike: Int,
    val countComment: Int,
    var avatarAuthor: String?,
    val description: String,
    val comment: List<String>,
    val newsImage: String
)

