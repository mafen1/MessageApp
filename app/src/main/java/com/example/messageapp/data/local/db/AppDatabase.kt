package com.example.messageapp.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.messageapp.data.local.db.dao.ChatDao
import com.example.messageapp.data.local.db.dao.MessageDao
import com.example.messageapp.data.local.db.dao.PendingMessageDao
import com.example.messageapp.data.local.db.entity.ChatEntity
import com.example.messageapp.data.local.db.entity.MessageEntity
import com.example.messageapp.data.local.db.entity.PendingMessageEntity
import com.example.messageapp.domain.model.MessageStatus

@Database(
    entities = [MessageEntity::class, ChatEntity::class, PendingMessageEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(MessageStatusConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
    abstract fun chatDao(): ChatDao
    abstract fun pendingMessageDao(): PendingMessageDao
}

class MessageStatusConverter {
    @TypeConverter
    fun fromMessageStatus(status: MessageStatus): String = status.name

    @TypeConverter
    fun toMessageStatus(name: String): MessageStatus =
        MessageStatus.entries.find { it.name == name } ?: MessageStatus.SENT
}
