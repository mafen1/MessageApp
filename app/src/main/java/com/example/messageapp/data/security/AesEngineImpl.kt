package com.example.messageapp.data.security

import com.example.messageapp.domain.security.AesEngine
import com.example.messageapp.domain.security.Base64Codec
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class AesEngineImpl @Inject constructor(
    private val base64Codec: Base64Codec
) : AesEngine {

    override fun encrypt(key: ByteArray, plaintext: String): String {
        val iv = ByteArray(IV_SIZE).apply { SecureRandom().nextBytes(this) }
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.ENCRYPT_MODE,
            SecretKeySpec(key, ALGORITHM),
            GCMParameterSpec(TAG_LENGTH_BITS, iv)
        )
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        val combined = iv + ciphertext
        return AesEngine.ENCRYPTED_PREFIX + base64Codec.encode(combined)
    }

    override fun decrypt(key: ByteArray, ciphertext: String): String {
        if (!ciphertext.startsWith(AesEngine.ENCRYPTED_PREFIX)) return ciphertext

        val combined = base64Codec.decode(
            ciphertext.removePrefix(AesEngine.ENCRYPTED_PREFIX)
        )
        if (combined.size < IV_SIZE) return ciphertext

        val iv = combined.copyOfRange(0, IV_SIZE)
        val encrypted = combined.copyOfRange(IV_SIZE, combined.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(key, ALGORITHM),
            GCMParameterSpec(TAG_LENGTH_BITS, iv)
        )
        return String(cipher.doFinal(encrypted), Charsets.UTF_8)
    }

    private companion object {
        private const val ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_SIZE = 12
        private const val TAG_LENGTH_BITS = 128
    }
}
