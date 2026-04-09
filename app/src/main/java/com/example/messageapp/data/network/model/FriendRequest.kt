package com.example.messageapp.data.network.model

import com.google.gson.annotations.SerializedName

data class FriendRequest(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("senderUserName") val senderUserName: String,
    @SerializedName("receiverUserName") val receiverUserName: String,
    @SerializedName("status") val status: String = "pending"
)

data class AcceptFriendRequest(
    @SerializedName("senderUserName") val senderUserName: String,
    @SerializedName("receiverUserName") val receiverUserName: String
)

data class FriendResponse(
    @SerializedName("message") val message: String,
    @SerializedName("friends") val friends: List<String>? = null,
    @SerializedName("requests") val requests: List<FriendRequest>? = null
)
