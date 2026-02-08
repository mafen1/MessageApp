package com.example.messageapp.data.network.model

import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName

data class NewsUploadWithOutImage(
    @SerializedName("id")
    val id: Int,
    @SerializedName("userName")
    val userName: String,
    @SerializedName("description")
    val description: String,
)
