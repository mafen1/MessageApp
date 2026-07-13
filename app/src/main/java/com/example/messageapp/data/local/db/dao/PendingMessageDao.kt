package com.example.messageapp.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.messageapp.data.local.db.entity.PendingMessageEntity

@Dao
interface PendingMessageDao {

    @Query("SELECT * FROM pending_messages ORDER BY createdAt ASC")
    suspend fun getAll(): List<PendingMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: PendingMessageEntity)

    @Query("DELETE FROM pending_messages WHERE clientMessageId = :clientMessageId")
    suspend fun delete(clientMessageId: String)

    @Query("UPDATE pending_messages SET retryCount = retryCount + 1 WHERE clientMessageId = :clientMessageId")
    suspend fun incrementRetry(clientMessageId: String)
}
