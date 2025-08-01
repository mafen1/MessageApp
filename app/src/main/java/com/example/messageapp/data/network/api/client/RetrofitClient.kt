package com.example.messageapp.data.network.api.client

import com.example.messageapp.data.network.api.service.ApiService
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val url = "http://10.0.2.2:8081"

    private val client = OkHttpClient.Builder()
        .build()


    val gson = GsonBuilder()
        .setLenient()
        .create()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(url)
            .build()
            .create(ApiService::class.java)
    }
}

