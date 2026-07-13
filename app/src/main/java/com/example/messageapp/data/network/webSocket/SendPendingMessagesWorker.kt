package com.example.messageapp.data.network.webSocket

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.data.local.db.dao.PendingMessageDao
import com.example.messageapp.data.local.preferences.PreferencesDataStore
import com.example.messageapp.domain.model.Message
import com.example.messageapp.domain.model.MessageStatus
import com.example.messageapp.domain.repository.ChatSocketRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class SendPendingMessagesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val chatSocketRepository: ChatSocketRepository,
    private val pendingMessageDao: PendingMessageDao,
    private val preferencesDataStore: PreferencesDataStore
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val userName = preferencesDataStore.getString(ConstVariables.userName).first()
        val token = preferencesDataStore.getString(ConstVariables.tokenJWT).first()
        if (userName.isBlank() || token.isBlank()) {
            return Result.retry()
        }

        chatSocketRepository.connect(userName, token)
        try {
            val pending = pendingMessageDao.getAll()
            if (pending.isEmpty()) {
                return Result.success()
            }

            var allSent = true
            for (item in pending) {
                val message = Message(
                    clientMessageId = item.clientMessageId,
                    senderUsername = userName,
                    recipientUsername = item.recipientUsername,
                    text = item.text,
                    isFromMe = true,
                    type = item.type,
                    status = MessageStatus.SENDING,
                    timestamp = item.createdAt
                )

                val maxRetries = 5
                if (item.retryCount >= maxRetries) {
                    pendingMessageDao.delete(item.clientMessageId)
                    continue
                }

                try {
                    chatSocketRepository.sendMessage(message)
                    pendingMessageDao.delete(item.clientMessageId)
                } catch (e: Exception) {
                    pendingMessageDao.incrementRetry(item.clientMessageId)
                    allSent = false
                }
            }

            return if (allSent) Result.success() else Result.retry()
        } finally {
            chatSocketRepository.disconnect()
        }
    }
}
