package com.example.messageapp.data.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@Serializable
data class UserResponse(
    @SerializedName("username") val userName: String,
    @SerializedName("name") val name: String
)