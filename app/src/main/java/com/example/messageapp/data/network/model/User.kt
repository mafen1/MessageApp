package com.example.messageapp.data.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import retrofit2.http.Body


@Serializable
data class User (
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("friend") val friend: List<String>?,
    @SerializedName("token") val token: String?
): java.io.Serializable