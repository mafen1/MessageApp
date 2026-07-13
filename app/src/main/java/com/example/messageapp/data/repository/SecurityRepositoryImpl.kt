package com.example.messageapp.data.repository

import android.util.Log
import com.example.messageapp.data.network.api.service.ApiService
import com.example.messageapp.data.network.model.PublicKeyRequest
import com.example.messageapp.data.network.model.WrappedKeyRequest
import com.example.messageapp.domain.repository.SecurityRepository
import com.example.messageapp.domain.security.Base64Codec
import com.example.messageapp.domain.security.EncryptionManager
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val encryptionManager: EncryptionManager,
    private val base64Codec: Base64Codec
) : SecurityRepository {

    override suspend fun uploadLocalPublicKey(): Result<Unit> = safeCall {
        val encoded = base64Codec.encode(encryptionManager.getLocalPublicKey().encoded)
        apiService.uploadPublicKey(PublicKeyRequest(encoded))
    }

    override suspend fun getPublicKey(username: String): Result<PublicKey> = safeCall {
        val response = apiService.getPublicKey(username)
        val bytes = base64Codec.decode(response.publicKey)
        val keyFactory = java.security.KeyFactory.getInstance("RSA")
        keyFactory.generatePublic(X509EncodedKeySpec(bytes))
    }

    override suspend fun uploadWrappedChatKey(
        chatId: String,
        recipientUsername: String,
        wrappedKey: String
    ): Result<Unit> = safeCall {
        apiService.uploadWrappedChatKey(
            WrappedKeyRequest(chatId, recipientUsername, wrappedKey)
        )
    }

    override suspend fun getWrappedChatKey(chatId: String, recipientUsername: String): Result<String> = safeCall {
        apiService.getWrappedChatKey(chatId, recipientUsername).wrappedKey
    }

    private suspend fun <T> safeCall(call: suspend () -> T): Result<T> {
        return try {
            Result.success(call())
        } catch (e: Exception) {
            Log.e("SecurityRepository", "Security operation failed", e)
            Result.failure(e)
        }
    }
}
