package com.example.messageapp.di

import com.example.messageapp.data.repository.ApiRepositoryImpl
import com.example.messageapp.domain.repository.AuthRepository
import com.example.messageapp.domain.repository.FriendRepository
import com.example.messageapp.domain.repository.MessageRepository
import com.example.messageapp.domain.repository.NewsRepository
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
    abstract fun bindAuthRepository(impl: ApiRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: ApiRepositoryImpl): UserRepository

    @Binds
    @Singleton
    abstract fun bindFriendRepository(impl: ApiRepositoryImpl): FriendRepository

    @Binds
    @Singleton
    abstract fun bindMessageRepository(impl: ApiRepositoryImpl): MessageRepository

    @Binds
    @Singleton
    abstract fun bindNewsRepository(impl: ApiRepositoryImpl): NewsRepository
}
