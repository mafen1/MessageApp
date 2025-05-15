package com.example.messageapp.data.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

// todo password переименовать
@Serializable
data class LoginRequest(
    @SerializedName("name") var userName: String,
    @SerializedName("username") var password: String
)
