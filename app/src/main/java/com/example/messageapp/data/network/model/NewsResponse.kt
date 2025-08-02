package com.example.messageapp.data.network.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import okhttp3.MultipartBody

@Serializable
data class NewsResponse(
    var id: Int,
    var userName: String,
    var image: String,
    var text: String
)

