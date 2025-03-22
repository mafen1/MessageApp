package com.example.messageapp.data.network.api.client

import com.example.messageapp.data.network.api.service.ApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object RetrofitClient {
    private const val url = "http://127.0.0.1:8081"

    private val client = OkHttpClient.Builder()
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .client(client)
            .baseUrl(url)
            .build()
            .create(ApiService::class.java)
    }

}

