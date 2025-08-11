package com.example.messageapp.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val username: String
)