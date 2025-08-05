package com.example.messageapp.data.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerializedName("name") var name: String,
    @SerializedName("username") var userName: String,
    @SerializedName("password") var password: String
)
