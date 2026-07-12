package com.example.messageapp.data.network.api.client

import com.example.messageapp.core.ConstVariables
import com.example.messageapp.domain.repo.preferences.AppPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val appPreference: AppPreference) : Interceptor {

    private val noAuthPaths = setOf("/register", "/login")

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (path in noAuthPaths) {
            return chain.proceed(request)
        }

        val token = runBlocking {
            runCatching { appPreference.getStringValue(ConstVariables.tokenJWT).first() }.getOrDefault("")
        }

        val newRequest = if (token.isNotBlank()) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        return chain.proceed(newRequest)
    }
}
