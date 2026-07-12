package com.example.messageapp.ui.components

import com.example.messageapp.BuildConfig
import com.example.messageapp.core.TokenStorage

fun imageUrl(fileName: String): String {
    return "${BuildConfig.BASE_URL}/images/$fileName?token=${TokenStorage.getToken()}"
}
