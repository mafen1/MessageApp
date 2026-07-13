# MessageApp — Phase 3: Offline-first Room foundation

## Goal

Persist chats and messages locally so the app works without network. Room becomes the single source of truth for chat history.

## Scope

- Room database with entities: `MessageEntity`, `ChatEntity`.
- DAOs with suspend queries and Flow observations.
- Database module in Hilt.
- `MessageRepository` reads/writes through Room; remote history populates local cache.
- Outbox table for messages pending send (preparation for WorkManager in Phase 3b).

## Out of scope
- WorkManager sync (Phase 3b).
- FTS5 search (Phase 3c).
- Full conflict resolution with server.

## Files

- `data/local/db/AppDatabase.kt`
- `data/local/db/dao/MessageDao.kt`
- `data/local/db/dao/ChatDao.kt`
- `data/local/db/entity/MessageEntity.kt`
- `data/local/db/entity/ChatEntity.kt`
- `data/local/db/entity/PendingMessageEntity.kt`
- `data/mapper/MessageMapper.kt` (extend with entity↔domain)
- `di/DatabaseModule.kt`
- Update `data/repository/ApiRepositoryImpl.kt` to use DAOs for messages.

## Success criteria

1. `./gradlew :app:assembleDebug` passes.
2. Chat history is loaded from Room first, then refreshed from remote.
3. New incoming/outgoing messages are inserted into Room.
