package com.example.messageapp.data.network.model

import com.google.gson.annotations.SerializedName

data class PublicKeyRequest(
    @SerializedName("publicKey") val publicKey: String
)

data class PublicKeyResponse(
    @SerializedName("publicKey") val publicKey: String
)

data class WrappedKeyRequest(
    @SerializedName("chatId") val chatId: String,
    @SerializedName("recipientUsername") val recipientUsername: String,
    @SerializedName("wrappedKey") val wrappedKey: String
)

data class WrappedKeyResponse(
    @SerializedName("chatId") val chatId: String,
    @SerializedName("wrappedKey") val wrappedKey: String
)
