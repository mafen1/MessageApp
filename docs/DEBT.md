# Technical Debt

## Phase 0–1 (resolved)
- [x] Migrated UI to Jetpack Compose with Material 3 dark-blue theme.
- [x] Established Clean Architecture layers (`domain`, `data`, `ui`).
- [x] Replaced legacy `ApiRepository` / `ApiServiceImpl` / `ApiServiceUseCase` with focused repositories and use cases.
- [x] Moved preferences implementation out of `domain` so the domain layer has no Android/DataStore imports.
- [x] Split Hilt modules into `NetworkModule`, `DataStoreModule`, `RepositoryModule`, `UseCaseModule`.
- [x] Removed unused XML layouts, fragments, adapters, menus, and navigation resources.

## Phase 2 (mostly resolved)
- [x] Android WebSocket manager has state machine (`SocketState`) exposed as `StateFlow`.
- [x] Automatic reconnect with exponential backoff (1s → 2s → 4s → 8s → 30s).
- [x] Outbox queue for messages sent while disconnected.
- [x] `clientMessageId` generated per outgoing message; UI deduplicates by it.
- [x] JWT passed via `Authorization` header instead of URL query (not leaked into logs).
- [ ] Server-side deduplication and delivery receipts for retried messages.
- [ ] Protobuf binary framing instead of colon-delimited text frames.

## Phase 3 (resolved)
- [x] Room database with `MessageEntity`, `ChatEntity`, `PendingMessageEntity`.
- [x] DAOs with Flow/suspend queries.
- [x] Hilt `DatabaseModule`.
- [x] `MessageRepositoryImpl` caches history in Room and falls back to local on remote failure.
- [x] Every incoming message is persisted on arrival (works outside the open chat screen).
- [x] WorkManager sync for outbox (`SendPendingMessagesWorker` with network constraint, retry limit = 5).
- [ ] FTS5 full-text search over message history.

## Phase 4
- [ ] Foreground service for background WebSocket.
- [ ] Notification channel and direct-reply notifications.
- [ ] Voice messages, image compression, BlurHash.
- [ ] Unread counters / last-message preview in chat list.

## Phase 5 (E2E resolved)
- [x] E2E encryption: per-chat AES-GCM keys, RSA key wrapping via Android Keystore,
      server-side wrapped-key storage with versioned epochs, self-healing key rotation
      (`ChatKeyResolver`).
- [x] Logout wipes local history (Room), chat keys and session.
- [ ] Threads/replies.
- [ ] "Read" delivery status timeline.
- [ ] Import/export of chat data.

## Phase 6 (tests resolved)
- [x] Client unit tests: 26 (encryption/key resolution 19, repositories, ViewModels).
- [x] Server integration tests: 5 (auth flow, JWT, chat-key access control) via Ktor testApplication + H2.
- [ ] CI/CD pipeline, Crashlytics.

## Security hardening log
- [x] `/messages/{user1}/{user2}` restricted to chat participants (was IDOR).
- [x] Message delivery restricted to friends server-side.
- [x] `allowBackup=false`, cleartext only for dev hosts via network security config,
      unused permissions removed.
- [x] Release build: R8 minify + resource shrink + keep rules; API logs gated by `BuildConfig.DEBUG`.
