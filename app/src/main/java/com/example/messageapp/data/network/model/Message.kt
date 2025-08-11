package com.example.messageapp.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val message: String,
    val isType: Boolean
)
