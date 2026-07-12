package com.example.messageapp.data.network.model

import com.google.gson.annotations.SerializedName

data class LikeRequest(
    @SerializedName("newsId") val newsId: Int,
    @SerializedName("userName") val userName: String
)

data class CommentRequest(
    @SerializedName("newsId") val newsId: Int,
    @SerializedName("userName") val userName: String,
    @SerializedName("text") val text: String
)
