package com.example.messageapp.data.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import com.example.messageapp.domain.security.LocalKeyStore
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PublicKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidLocalKeyStore @Inject constructor() : LocalKeyStore {

    override fun getOrCreateKeyPair(): KeyPair {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }

        if (keyStore.containsAlias(KEY_ALIAS)) {
            val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.PrivateKeyEntry
            return KeyPair(entry.certificate.publicKey, entry.privateKey)
        }

        val generator = KeyPairGenerator.getInstance(ALGORITHM, ANDROID_KEY_STORE)
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_DECRYPT
        )
            .setDigests(KeyProperties.DIGEST_SHA256)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
            .setKeySize(KEY_SIZE)
            .build()

        generator.initialize(spec)
        return generator.generateKeyPair()
    }

    override fun getPublicKey(): PublicKey = getOrCreateKeyPair().public

    private companion object {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val ALGORITHM = "RSA"
        private const val KEY_ALIAS = "message_app_rsa"
        private const val KEY_SIZE = 2048
    }
}
