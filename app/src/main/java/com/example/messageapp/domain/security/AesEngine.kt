package com.example.messageapp.domain.security

interface AesEngine {
    fun encrypt(key: ByteArray, plaintext: String): String
    fun decrypt(key: ByteArray, ciphertext: String): String

    companion object {
        const val ENCRYPTED_PREFIX = "ENC:"
    }
}
