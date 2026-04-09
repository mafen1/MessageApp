package com.example.messageapp.data.network.api.client

import android.util.Log
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.data.network.api.service.ApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private val gson: Gson = GsonBuilder()
        .serializeNulls()
        .setStrictness(Strictness.LENIENT)
        .setPrettyPrinting()
        .create()

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d("NETWORK_DEBUG", message)
    }.apply {
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

            val responseBody = response.body?.string() ?: ""
            Log.d("NETWORK_DEBUG", "=== RESPONSE ===")
            Log.d("NETWORK_DEBUG", "Code: ${response.code}")
            Log.d("NETWORK_DEBUG", "Headers: ${response.headers}")
            Log.d("NETWORK_DEBUG", "Body: $responseBody")

            val contentType = when {
                responseBody.trim().startsWith("{") || responseBody.trim().startsWith("[") ->
                    "application/json".toMediaTypeOrNull()
                else -> response.body.contentType()
            }

            response.newBuilder()
                .body(ResponseBody.create(contentType, responseBody))
                .build()
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private class LenientGsonConverterFactory private constructor(private val gson: Gson) : Converter.Factory() {
        companion object {
            fun create(gson: Gson): LenientGsonConverterFactory {
                return LenientGsonConverterFactory(gson)
            }
        }

        override fun responseBodyConverter(
            type: Type,
            annotations: Array<out Annotation>,
            retrofit: Retrofit
        ): Converter<ResponseBody, *> {
            val adapter = gson.getAdapter(TypeToken.get(type))
            return LenientGsonResponseBodyConverter(gson, adapter)
        }

        private class LenientGsonResponseBodyConverter<T>(
            private val gson: Gson,
            private val adapter: com.google.gson.TypeAdapter<T>
        ) : Converter<ResponseBody, T> {
            override fun convert(value: ResponseBody): T? {
                val contentType = value.contentType()
                val body = value.string()

                if (body.isEmpty()) {
                    return null
                }

                val trimmedBody = body.trim()
                if (!trimmedBody.startsWith("{") && !trimmedBody.startsWith("[")) {
                    Log.w("NETWORK_DEBUG", "Non-JSON response received: $trimmedBody")
                    return null
                }

                return try {
                    val jsonReader = gson.newJsonReader(body.reader())
                    jsonReader.setStrictness(Strictness.LENIENT)
                    adapter.read(jsonReader)
                } catch (e: Exception) {
                    Log.e("NETWORK_DEBUG", "JSON parse error: ${e.message}")
                    null
                }
            }
        }
    }

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(ConstVariables.url)
            .client(okHttpClient)
            .addConverterFactory(LenientGsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}
