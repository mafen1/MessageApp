package com.example.messageapp.data.network.api.service

import retrofit2.http.GET

interface ApiService {
    @GET("/chat")
    suspend fun chat (message: String): String
}