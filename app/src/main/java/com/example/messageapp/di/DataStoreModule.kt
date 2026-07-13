package com.example.messageapp.di

import com.example.messageapp.data.local.preferences.PreferencesDataStore
import com.example.messageapp.domain.repository.PreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataStoreModule {

    @Binds
    @Singleton
    abstract fun bindPreferencesRepository(
        preferencesDataStore: PreferencesDataStore
    ): PreferencesRepository
}
