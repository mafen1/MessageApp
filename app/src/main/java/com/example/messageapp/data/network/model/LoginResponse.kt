package com.example.messageapp.data.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("expiresAt") val expiresAt: String,
    @SerializedName("user") val user: User
)
