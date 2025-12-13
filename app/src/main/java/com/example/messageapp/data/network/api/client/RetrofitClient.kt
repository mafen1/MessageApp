package com.example.messageapp.data.network.api.client

import android.util.Log
import com.example.messageapp.data.network.api.service.ApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8081"

    private val gson = GsonBuilder()
        .serializeNulls()
        .setStrictness(Strictness.LENIENT)
        .setPrettyPrinting() // Для красивого вывода JSON в логах
        .create()

    private val loggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            Log.d("NETWORK_DEBUG", message) // Кастомный тег для удобства
        }
    }).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val request = chain.request()
            val requestBody = request.body?.let {
                val buffer = Buffer()
                it.writeTo(buffer)
                buffer.readUtf8()
            }

            Log.d("NETWORK_DEBUG", "=== REQUEST ===")
            Log.d("NETWORK_DEBUG", "URL: ${request.url}")
            Log.d("NETWORK_DEBUG", "Method: ${request.method}")
            Log.d("NETWORK_DEBUG", "Headers: ${request.headers}")
            Log.d("NETWORK_DEBUG", "Body: $requestBody")

            val response = chain.proceed(request)

            val responseBody = response.body?.string()
            Log.d("NETWORK_DEBUG", "=== RESPONSE ===")
            Log.d("NETWORK_DEBUG", "Code: ${response.code}")
            Log.d("NETWORK_DEBUG", "Headers: ${response.headers}")
            Log.d("NETWORK_DEBUG", "Body: $responseBody")

            // Важно: создаем новый response с прочитанным body
            response.newBuilder()
                .body(ResponseBody.create(response.body?.contentType(), responseBody ?: ""))
                .build()
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}