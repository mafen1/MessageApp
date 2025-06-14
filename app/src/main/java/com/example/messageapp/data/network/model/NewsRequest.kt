package com.example.messageapp.data.network.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import okhttp3.MultipartBody

@Serializable
data class NewsRequest(
    var id: Int,
    var userName: String,
    @Contextual
    var image: MultipartBody.Part,
    var text: String
)
