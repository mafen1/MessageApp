# MessageApp — Phase 2: WebSocket state machine and reliability

## Goal

Make the messenger's real-time layer reliable: explicit connection state, automatic reconnect with backoff, heartbeat, and message deduplication. Keep the existing string protocol working; protobuf framing is Phase 2b.

## Scope

### In scope
- Android WebSocket manager with state machine exposed as `StateFlow`.
- Exponential backoff reconnect (1s → 2s → 4s → 8s → 30s max).
- Heartbeat: ping every 30s; if no pong within 10s, treat as dead and reconnect.
- Client-generated `clientMessageId` (UUID) for every outgoing message.
- Local pending message queue: messages sent while offline are stored and retried once connected.
- Delivery status: `SENDING → SENT → DELIVERED` (server ack required).

### Out of scope (Phase 2b/3)
- Protobuf binary framing.
- Server-side deduplication and delivery receipts.
- Room persistence.

## Architecture

### New domain model

```kotlin
data class OutgoingMessage(
    val clientMessageId: String,
    val chatId: String,
    val recipientUserName: String,
    val text: String,
    val timestamp: Long,
    val status: MessageStatus
)
```

### New repository interface

```kotlin
interface ChatSocketRepository {
    val connectionState: StateFlow<SocketState>
    fun connect(userName: String, token: String)
    fun disconnect()
    suspend fun sendMessage(message: OutgoingMessage)
    fun observeMessages(): Flow<IncomingMessage>
}

sealed class SocketState {
    data object Disconnected : SocketState()
    data object Connecting : SocketState()
    data object Connected : SocketState()
    data object Authenticated : SocketState()
    data class Error(val reason: String) : SocketState()
}
```

### Implementation

`ChatWebSocketManager` (data layer) wraps `org.java_websocket.client.WebSocketClient`:
- Holds `MutableStateFlow<SocketState>`.
- On `connect()`: create client, set state `Connecting`, connect.
- On `onOpen()`: send auth frame (`auth:username:token`), move to `Authenticated`.
- On `onMessage()`: parse incoming frame; emit to `SharedFlow<IncomingMessage>`.
- On `onClose()`/`onError()`: move to `Error`, schedule reconnect.
- Heartbeat: launch coroutine that sends `ping` every 30s; if no `pong` response within 10s, force reconnect.
- Outbox: `Channel<OutgoingMessage>`; messages are sent when state is `Authenticated`, otherwise queued.

### Mapping to existing protocol

Outgoing text frame format (unchanged for server compatibility):
```
<recipient>:<messageType>:<clientMessageId>:<text>
```

Incoming text frame format:
```
<sender>:<messageType>:<clientMessageId>:<text>
```

Server currently sends `sender:messageType:text`. We'll make Android parse both formats.

## Files to change

- Create `domain/model/OutgoingMessage.kt`, `domain/model/IncomingMessage.kt`, `domain/repository/ChatSocketRepository.kt`.
- Create `data/network/webSocket/ChatWebSocketManager.kt`.
- Delete `data/network/webSocket/client/ChatWebSocketClient.kt` and `WebSocketClient.kt`.
- Update `data/repository/ApiRepositoryImpl.kt` or create `MessageRepositoryImpl.kt` to use `ChatSocketRepository`.
- Update `di/NetworkModule.kt` or `RepositoryModule.kt` to provide the socket manager.
- Update `ui/screen/chat/ChatViewModel.kt` and `ChatScreen.kt` to show connection state.

## Success criteria

1. `./gradlew :app:assembleDebug` passes.
2. Domain layer still has no Android/network imports.
3. Unit test: simulated disconnect triggers reconnect with exponential backoff.
4. Unit test: message sent while disconnected is queued and sent after reconnect.
