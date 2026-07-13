# MessageApp — Phase 3b: WorkManager offline sync

## Goal

Ensure pending chat messages are reliably sent when the device regains network connectivity. Use WorkManager with a network constraint to process the outbox.

## Scope

- When `ChatWebSocketManager` cannot send immediately, persist the message to `pending_messages` table.
- `SendPendingMessagesWorker` reads the outbox and sends each message via `ChatSocketRepository`.
- WorkManager constraints: `NetworkType.CONNECTED`.
- Retry policy: exponential backoff, max attempts per message = 5.
- On success: delete from pending table and save as `SENT` in messages table.
- On final failure: mark as `FAILED` in messages table and remove from pending.

## Architecture

### Updated flow

```
ChatScreen send
  → ChatViewModel.sendTextMessage
    → ChatSocketRepository.sendMessage
      → if connected: send frame
      → if not connected: insert PendingMessageEntity + enqueue SendPendingMessagesWorker
```

### Worker

```kotlin
class SendPendingMessagesWorker(
    context: Context,
    params: WorkerParameters,
    private val chatSocketRepository: ChatSocketRepository,
    private val pendingMessageDao: PendingMessageDao,
    private val messageDao: MessageDao
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val pending = pendingMessageDao.getAll()
        for (item in pending) {
            // send via repository, await authentication
            // on success: delete pending, update message status
            // on failure: increment retry, delete if max retries reached
        }
        return Result.success() // or retry if any left
    }
}
```

### Hilt worker support

Add `androidx.hilt:hilt-work` and `@HiltWorker` so Worker can receive dependencies via constructor injection.

## Files

- `data/worker/SendPendingMessagesWorker.kt`
- `data/repository/ApiRepositoryImpl.kt` or new `MessageRepositoryImpl.kt` — insert pending messages
- Update `ChatWebSocketManager.sendMessage` to fall back to pending table
- Update `di/NetworkModule.kt` or create `di/WorkerModule.kt`
- `app/App.kt` — configure `Configuration.Provider` for Hilt workers

## Success criteria

1. `./gradlew :app:assembleDebug` passes.
2. `./gradlew :app:testDebugUnitTest` passes.
3. Sending a message while offline creates a row in `pending_messages`.
4. Enabling network triggers WorkManager to process the outbox.
