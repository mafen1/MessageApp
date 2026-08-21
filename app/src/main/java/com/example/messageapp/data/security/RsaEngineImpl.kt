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
        // PKCS1 вместо OAEP: keymint на новых эмуляторах (API 33+) не даёт
        // авторизовать MGF1-дайджест через KeyGenParameterSpec и OAEP-unwrap
        // падает (INCOMPATIBLE_MGF_DIGEST / UNKNOWN_ERROR на finish).
        // Обёртка статична (лежит на сервере), а не расшифровывается по оракулу,
        // поэтому риски padding-oracle неприменимы
        private const val TRANSFORMATION = "RSA/ECB/PKCS1Padding"
    }
}
