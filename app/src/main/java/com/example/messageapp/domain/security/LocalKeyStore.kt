package com.example.messageapp.domain.security

import java.security.KeyPair
import java.security.PublicKey

interface LocalKeyStore {
    fun getOrCreateKeyPair(): KeyPair
    fun getPublicKey(): PublicKey
}
