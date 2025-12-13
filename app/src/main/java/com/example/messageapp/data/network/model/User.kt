package com.example.messageapp.data.network.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//
//@Serializable
//data class User(
//    @SerialName("id") val id: Int,
//    @SerialName("name") val name: String,
//    @SerialName("username") val userName: String,
//    @SerialName("listUserName") val friend: List<String>?,
//    @SerialName("token") val token: String?,
//    @SerialName("password") val password: String?
//) : java.io.Serializable

//@Serializable
//data class User(
//    val id: Int = 0,
//    val name: String = "",
//    @SerialName("username")
//    val userName: String = "", // Используем точно такое же имя поля как в JSON
//    val friend: List<String>? = emptyList(), // Non-null с дефолтом
//    val token: String = "", // Non-null с пустой строкой по умолчанию
//    @Transient // ← Игнорируем это поле при сериализации
//    val password: String?
//): java.io.Serializable

data class User(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("username") val userName: String = "", // Важно: username в JSON
    @SerializedName("listUserName") val friend: List<String>? = emptyList(),
    @SerializedName("token") val token: String = "",
    @SerializedName("password") val password: String? = null
) : java.io.Serializable