package com.example.messageapp.di

import android.content.Context
import androidx.room.Room
import com.example.messageapp.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "message_app.db"
        ).build()
    }

    @Provides
    fun provideMessageDao(db: AppDatabase) = db.messageDao()

    @Provides
    fun provideChatDao(db: AppDatabase) = db.chatDao()

    @Provides
    fun providePendingMessageDao(db: AppDatabase) = db.pendingMessageDao()
}
