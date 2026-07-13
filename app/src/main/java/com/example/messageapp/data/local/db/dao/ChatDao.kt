package com.example.messageapp.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.messageapp.data.local.db.entity.ChatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Query("SELECT * FROM chats ORDER BY lastMessageTime DESC")
    fun observeChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM chats ORDER BY lastMessageTime DESC")
    suspend fun getChats(): List<ChatEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chat: ChatEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chats: List<ChatEntity>)

    @Query("DELETE FROM chats WHERE id = :chatId")
    suspend fun delete(chatId: String)
}
