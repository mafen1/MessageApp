package com.example.messageapp.di

import com.example.messageapp.data.security.AesEngineImpl
import com.example.messageapp.data.security.AndroidBase64Codec
import com.example.messageapp.data.security.AndroidLocalKeyStore
import com.example.messageapp.data.security.EncryptedSharedPreferencesChatKeyStorage
import com.example.messageapp.data.security.EncryptionManagerImpl
import com.example.messageapp.data.security.RsaEngineImpl
import com.example.messageapp.domain.security.AesEngine
import com.example.messageapp.domain.security.Base64Codec
import com.example.messageapp.domain.security.ChatKeyStorage
import com.example.messageapp.domain.security.EncryptionManager
import com.example.messageapp.domain.security.LocalKeyStore
import com.example.messageapp.domain.security.RsaEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {

    @Binds
    @Singleton
    abstract fun bindEncryptionManager(impl: EncryptionManagerImpl): EncryptionManager

    @Binds
    @Singleton
    abstract fun bindLocalKeyStore(impl: AndroidLocalKeyStore): LocalKeyStore

    @Binds
    @Singleton
    abstract fun bindChatKeyStorage(impl: EncryptedSharedPreferencesChatKeyStorage): ChatKeyStorage

    @Binds
    @Singleton
    abstract fun bindAesEngine(impl: AesEngineImpl): AesEngine

    @Binds
    @Singleton
    abstract fun bindRsaEngine(impl: RsaEngineImpl): RsaEngine

    @Binds
    @Singleton
    abstract fun bindBase64Codec(impl: AndroidBase64Codec): Base64Codec
}
