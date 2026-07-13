package com.example.messageapp.domain.security

import java.security.KeyPair
import java.security.PublicKey

interface RsaEngine {
    fun wrapKey(secretKey: ByteArray, publicKey: PublicKey): ByteArray
    fun unwrapKey(wrappedKey: ByteArray, keyPair: KeyPair): ByteArray
}
