package com.example.messageapp.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class NewsRequest(
    var id: Int,
    var userName: String,
    var text: String
)
