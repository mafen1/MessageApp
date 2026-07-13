package com.example.messageapp.data.network.webSocket

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.messageapp.data.local.db.dao.MessageDao
import com.example.messageapp.data.local.db.dao.PendingMessageDao
import com.example.messageapp.data.local.db.entity.PendingMessageEntity
import com.example.messageapp.data.mapper.toDomain
import com.example.messageapp.data.mapper.toEntity
import com.example.messageapp.domain.model.Message
import com.example.messageapp.domain.model.MessageStatus
import com.example.messageapp.domain.model.SocketState
import com.example.messageapp.domain.repository.ChatSocketRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.min

@Singleton
class ChatWebSocketManager @Inject constructor(
    private val pendingMessageDao: PendingMessageDao,
    private val messageDao: MessageDao,
    private val workManager: WorkManager
) : ChatSocketRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _connectionState = MutableStateFlow<SocketState>(SocketState.Disconnected)
    override val connectionState: StateFlow<SocketState> = _connectionState.asStateFlow()

    private val _messages = MutableSharedFlow<Message>(extraBufferCapacity = 64)
    override fun observeMessages(): SharedFlow<Message> = _messages.asSharedFlow()

    private var client: WebSocketClient? = null
    private var currentUserName: String = ""
    private var currentToken: String = ""

    private val reconnectDelays = listOf(1000L, 2000L, 4000L, 8000L, 30000L)
    private var reconnectAttempt = 0
    @Volatile
    private var isClosing = false

    override fun connect(userName: String, token: String) {
        if (currentUserName == userName && currentToken == token &&
            _connectionState.value == SocketState.Authenticated
        ) {
            return
        }

        isClosing = false
        currentUserName = userName
        currentToken = token
        reconnectAttempt = 0
        disconnectInternal()
        createAndConnectClient()
    }

    override fun disconnect() {
        isClosing = true
        disconnectInternal()
        scope.cancel()
    }

    override suspend fun sendMessage(message: Message) {
        val messageWithId = if (message.clientMessageId.isBlank()) {
            message.copy(clientMessageId = UUID.randomUUID().toString(), status = MessageStatus.SENDING)
        } else {
            message.copy(status = MessageStatus.SENDING)
        }

        val chatId = chatId(message.senderUsername, message.recipientUsername)
        messageDao.insert(messageWithId.toEntity(chatId))

        if (_connectionState.value == SocketState.Authenticated) {
            sendFrame(messageWithId)
        } else {
            pendingMessageDao.insert(messageWithId.toPendingEntity(chatId))
            scheduleSendPendingMessages()
        }
    }

    private fun createAndConnectClient() {
        if (currentUserName.isBlank() || currentToken.isBlank()) return

        _connectionState.value = SocketState.Connecting

        val uri = URI("${com.example.messageapp.BuildConfig.WS_URL}/chat/$currentUserName?token=$currentToken")
        val newClient = object : WebSocketClient(uri) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d(TAG, "WebSocket opened")
                _connectionState.value = SocketState.Authenticated
                reconnectAttempt = 0
                scope.launch { drainPendingMessages() }
            }

            override fun onMessage(message: String?) {
                message ?: return
                Log.d(TAG, "Received: $message")
                parseIncomingMessage(message)?.let { _messages.tryEmit(it) }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(TAG, "WebSocket closed: $code $reason remote=$remote")
                if (!isClosing) {
                    scheduleReconnect()
                } else {
                    _connectionState.value = SocketState.Disconnected
                }
            }

            override fun onError(ex: Exception?) {
                Log.e(TAG, "WebSocket error", ex)
                _connectionState.value = SocketState.Error(ex?.message ?: "Unknown error")
                if (!isClosing) {
                    scheduleReconnect()
                }
            }
        }

        client = newClient
        try {
            newClient.connect()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to connect", e)
            _connectionState.value = SocketState.Error(e.message ?: "Connection failed")
            scheduleReconnect()
        }
    }

    private fun disconnectInternal() {
        client?.let {
            try {
                it.close()
            } catch (e: Exception) {
                Log.e(TAG, "Error closing WebSocket", e)
            }
        }
        client = null
    }

    private fun scheduleReconnect() {
        if (isClosing) return
        val delay = reconnectDelays.getOrElse(reconnectAttempt) { reconnectDelays.last() }
        reconnectAttempt = min(reconnectAttempt + 1, reconnectDelays.size - 1)
        _connectionState.value = SocketState.Connecting

        scope.launch {
            delay(delay)
            if (isActive && !isClosing) {
                createAndConnectClient()
            }
        }
    }

    private suspend fun drainPendingMessages() {
        val pending = pendingMessageDao.getAll()
        for (item in pending) {
            val message = item.toDomain()
            sendFrame(message)
        }
    }

    private suspend fun sendFrame(message: Message) {
        val client = client ?: return
        val chatId = chatId(message.senderUsername, message.recipientUsername)

        val frame = when (message.type) {
            "image" -> "to:${message.recipientUsername}:image:${message.clientMessageId}:${message.text}"
            else -> "to:${message.recipientUsername}:text:${message.clientMessageId}:${message.text}"
        }

        try {
            if (client.isOpen) {
                client.send(frame)
                pendingMessageDao.delete(message.clientMessageId)
                messageDao.insert(message.copy(status = MessageStatus.SENT).toEntity(chatId))
                _messages.tryEmit(message.copy(status = MessageStatus.SENT))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send message", e)
        }
    }

    private fun parseIncomingMessage(raw: String): Message? {
        val parts = raw.split(":", limit = 5)
        return when {
            parts.size >= 5 && parts[0] == "to" -> {
                val sender = parts[1]
                val type = parts[2]
                val clientId = parts[3]
                val text = parts[4]
                Message(
                    clientMessageId = clientId,
                    senderUsername = sender,
                    recipientUsername = currentUserName,
                    text = text,
                    isFromMe = sender == currentUserName,
                    type = type,
                    status = MessageStatus.DELIVERED
                )
            }

            parts.size >= 4 -> {
                val sender = parts[0]
                val type = parts[1]
                val clientId = parts[2]
                val text = parts[3]
                Message(
                    clientMessageId = clientId,
                    senderUsername = sender,
                    recipientUsername = currentUserName,
                    text = text,
                    isFromMe = sender == currentUserName,
                    type = type,
                    status = MessageStatus.DELIVERED
                )
            }

            parts.size >= 3 -> {
                val sender = parts[0]
                val text = parts[1]
                Message(
                    clientMessageId = UUID.randomUUID().toString(),
                    senderUsername = sender,
                    recipientUsername = currentUserName,
                    text = text,
                    isFromMe = sender == currentUserName,
                    type = "text",
                    status = MessageStatus.DELIVERED
                )
            }

            parts.size >= 2 -> {
                val sender = parts[0]
                val text = parts[1]
                Message(
                    clientMessageId = UUID.randomUUID().toString(),
                    senderUsername = sender,
                    recipientUsername = currentUserName,
                    text = text,
                    isFromMe = sender == currentUserName,
                    type = "text",
                    status = MessageStatus.DELIVERED
                )
            }

            else -> null
        }
    }

    private fun scheduleSendPendingMessages() {
        val request = OneTimeWorkRequestBuilder<SendPendingMessagesWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        workManager.enqueue(request)
    }

    private fun chatId(user1: String, user2: String): String {
        return listOf(user1, user2).sorted().joinToString("__")
    }

    companion object {
        private const val TAG = "ChatWebSocketManager"
    }
}

private fun PendingMessageEntity.toDomain(): Message = Message(
    clientMessageId = clientMessageId,
    senderUsername = "",
    recipientUsername = recipientUsername,
    text = text,
    isFromMe = true,
    type = type,
    status = MessageStatus.SENDING,
    timestamp = createdAt
)

private fun Message.toPendingEntity(chatId: String): PendingMessageEntity = PendingMessageEntity(
    clientMessageId = clientMessageId,
    chatId = chatId,
    recipientUsername = recipientUsername,
    text = text,
    type = type,
    createdAt = timestamp
)
