package com.example.messageapp.data.network.model

import com.google.gson.annotations.SerializedName

data class PublicKeyRequest(
    @SerializedName("publicKey") val publicKey: String
)

data class PublicKeyResponse(
    @SerializedName("publicKey") val publicKey: String
)

data class WrappedKeyEntry(
    @SerializedName("recipientUsername") val recipientUsername: String,
    @SerializedName("wrappedKey") val wrappedKey: String
)

data class PublishChatKeysRequest(
    @SerializedName("chatId") val chatId: String,
    @SerializedName("entries") val entries: List<WrappedKeyEntry>
)

data class PublishChatKeysResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("version") val version: Long
)

data class WrappedKeyResponse(
    @SerializedName("chatId") val chatId: String,
    @SerializedName("wrappedKey") val wrappedKey: String,
    @SerializedName("version") val version: Long
)
