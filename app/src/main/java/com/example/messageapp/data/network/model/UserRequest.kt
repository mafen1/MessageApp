package com.example.messageapp.data.network.model

import com.google.gson.annotations.SerializedName

data class UserRequest(
    @SerializedName("userName") val username: String
)