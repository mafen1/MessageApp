package com.example.messageapp.domain.security

interface Base64Codec {
    fun encode(bytes: ByteArray): String
    fun decode(encoded: String): ByteArray
}
