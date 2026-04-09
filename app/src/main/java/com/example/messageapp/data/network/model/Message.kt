package com.example.messageapp.data.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class Message(
    val message: String,
    val isType: Boolean
)

data class MessageResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val senderUsername: String,
    @SerializedName("recipientUsername") val recipientUsername: String,
    @SerializedName("message") val message: String
)
