package com.example.messageapp.data.network.model

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("userName") val userName: String,
    @SerializedName("name") val name: String,
    @SerializedName("password") val password: String? = null
)
