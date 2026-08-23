package com.example.messageapp.di

import com.example.messageapp.data.repository.AuthRepositoryImpl
import com.example.messageapp.data.repository.FriendRepositoryImpl
import com.example.messageapp.data.repository.MessageRepositoryImpl
import com.example.messageapp.data.repository.NewsRepositoryImpl
import com.example.messageapp.data.repository.SecurityRepositoryImpl
import com.example.messageapp.data.network.webSocket.ChatWebSocketManager
import com.example.messageapp.data.repository.UserRepositoryImpl
import com.example.messageapp.domain.repository.AuthRepository
import com.example.messageapp.domain.repository.ChatSocketRepository
import com.example.messageapp.domain.repository.FriendRepository
import com.example.messageapp.domain.repository.MessageRepository
import com.example.messageapp.domain.repository.NewsRepository
import com.example.messageapp.domain.repository.SecurityRepository
import com.example.messageapp.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindFriendRepository(impl: FriendRepositoryImpl): FriendRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(impl: MessageRepositoryImpl): MessageRepository

    @Binds
    @Singleton
    abstract fun bindNewsRepository(impl: NewsRepositoryImpl): NewsRepository

    @Binds
    @Singleton
    abstract fun bindChatSocketRepository(impl: ChatWebSocketManager): ChatSocketRepository

    @Binds
    @Singleton
    abstract fun bindSecurityRepository(impl: SecurityRepositoryImpl): SecurityRepository
}
