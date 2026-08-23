package com.example.messageapp.data.network.webSocket

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.data.local.db.dao.MessageDao
import com.example.messageapp.data.local.db.dao.PendingMessageDao
import com.example.messageapp.data.local.preferences.PreferencesDataStore
import com.example.messageapp.domain.model.Message
import com.example.messageapp.domain.model.MessageStatus
import com.example.messageapp.domain.model.SocketState
import com.example.messageapp.domain.repository.ChatSocketRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds

@HiltWorker
class SendPendingMessagesWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val chatSocketRepository: ChatSocketRepository,
    private val pendingMessageDao: PendingMessageDao,
    private val messageDao: MessageDao,
    private val preferencesDataStore: PreferencesDataStore
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val userName = preferencesDataStore.getString(ConstVariables.userName).first()
        val token = preferencesDataStore.getString(ConstVariables.tokenJWT).first()
        if (userName.isBlank() || token.isBlank()) {
            // не залогинен — ретраить бессмысленно
            return Result.failure()
        }

        // сокетом управляет Singleton-менеджер: воркер только просит подключение
        // (идемпотентно) и ждёт готовности. disconnect() здесь нельзя — иначе после
        // отправки очереди рвалась живая переписка, и сообщения «висели» до
        // перезахода в чат (фикс TC-10)
        chatSocketRepository.connect(userName, token)

        // ждём Authenticated с таймаутом вместо мгновенной проверки
        val authenticated = withTimeoutOrNull(CONNECT_TIMEOUT_MS.milliseconds) {
            chatSocketRepository.connectionState.first { it is SocketState.Authenticated }
            true
        }
        if (authenticated != true) {
            incrementAllRetries()
            return Result.retry()
        }

        return runCatching {
            val snapshot = pendingMessageDao.getAll()
            if (snapshot.isEmpty()) {
                return Result.success()
            }

            for (item in snapshot) {
                val maxRetries = MAX_RETRIES
                if (item.retryCount >= maxRetries) {
                    // помечаем FAILED в messages, чтобы UI не висел в SENDING (фикс M10)
                    pendingMessageDao.delete(item.clientMessageId)
                    messageDao.updateStatus(item.clientMessageId, MessageStatus.FAILED)
                    continue
                }

                try {
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
                    chatSocketRepository.sendMessage(message)
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to send pending ${item.clientMessageId}", e)
                }
            }

            // успех = сообщение исчезло из очереди (sendEncryptedFrame удаляет после реальной отправки;
            // при недоступном ключе оно остаётся в очереди и НЕ должно удаляться вслепую)
            val remaining = pendingMessageDao.getAll().map { it.clientMessageId }.toSet()
            var allSent = true
            for (item in snapshot) {
                if (item.retryCount >= MAX_RETRIES) continue
                if (item.clientMessageId in remaining) {
                    pendingMessageDao.incrementRetry(item.clientMessageId)
                    allSent = false
                }
            }

            if (allSent) Result.success() else Result.retry()
        }.getOrElse {
            Log.e(TAG, "Outbox drain failed", it)
            Result.retry()
        }
    }

    private suspend fun incrementAllRetries() {
        pendingMessageDao.getAll().forEach { pendingMessageDao.incrementRetry(it.clientMessageId) }
    }

    companion object {
        private const val TAG = "SendPendingWorker"
        private const val CONNECT_TIMEOUT_MS = 15_000L
        private const val MAX_RETRIES = 5
    }
}
