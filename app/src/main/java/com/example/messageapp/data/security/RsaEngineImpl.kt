package com.example.messageapp.data.security

import com.example.messageapp.domain.security.RsaEngine
import java.security.KeyPair
import java.security.PublicKey
import javax.crypto.Cipher
import javax.inject.Inject

class RsaEngineImpl @Inject constructor() : RsaEngine {

    override fun wrapKey(secretKey: ByteArray, publicKey: PublicKey): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        return cipher.doFinal(secretKey)
    }

    override fun unwrapKey(wrappedKey: ByteArray, keyPair: KeyPair): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, keyPair.private)
        return cipher.doFinal(wrappedKey)
    }

    private companion object {
        private const val TRANSFORMATION = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
    }
}
