package com.example.messageapp.di

import android.util.Log
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.data.network.api.service.ApiService
import com.example.messageapp.domain.repoImpl.ApiServiceImpl
import com.example.messageapp.domain.repo.apiRepository.ApiRepository
import com.example.messageapp.domain.repo.preferences.AppPreference
import com.example.messageapp.store.DataStore
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object Module {


    @Provides
    fun provideGson() : Gson = GsonBuilder()
        .setLenient()
        .create()

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("NETWORK_DEBUG", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideApiService(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): ApiService{
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(ConstVariables.url)
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun appPreference(dataStore: DataStore) : AppPreference = dataStore

    @Provides
    @Singleton
    fun apiServiceImpl(apiServiceImpl: ApiServiceImpl): ApiRepository = apiServiceImpl
}