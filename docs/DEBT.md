# Technical Debt

## Phase 0–1 (resolved)
- [x] Migrated UI to Jetpack Compose with Material 3 dark-blue theme.
- [x] Established Clean Architecture layers (`domain`, `data`, `ui`).
- [x] Replaced legacy `ApiRepository` / `ApiServiceImpl` / `ApiServiceUseCase` with focused repositories and use cases.
- [x] Moved preferences implementation out of `domain` so the domain layer has no Android/DataStore imports.
- [x] Split Hilt modules into `NetworkModule`, `DataStoreModule`, `RepositoryModule`, `UseCaseModule`.
- [x] Removed unused XML layouts, fragments, adapters, menus, and navigation resources.

## Phase 2 (partially resolved)
- [x] Android WebSocket manager has state machine (`SocketState`) exposed as `StateFlow`.
- [x] Automatic reconnect with exponential backoff (1s → 2s → 4s → 8s → 30s).
- [x] Outbox queue for messages sent while disconnected.
- [x] `clientMessageId` generated per outgoing message; UI deduplicates by it.
- [ ] Server WebSocket manager has no deduplication or delivery receipts.
- [ ] No protobuf binary framing.

## Phase 3 (partially resolved)
- [x] Room database with `MessageEntity`, `ChatEntity`, `PendingMessageEntity`.
- [x] DAOs with Flow/suspend queries.
- [x] Hilt `DatabaseModule`.
- [x] `MessageRepository` caches history in Room and falls back to local on remote failure.
- [x] Chat ViewModel persists messages via Room instead of JSON in preferences.
- [x] WorkManager sync for outbox (`SendPendingMessagesWorker` with network constraint, retry limit = 5).
- [ ] FTS5 search.

## Phase 4
- [ ] No foreground service for background WebSocket.
- [ ] No notification channel or direct-reply notifications.
- [ ] No voice messages, image compression, BlurHash.

## Phase 5
- [ ] No E2E encryption.
- [ ] No threads/replies.
- [ ] No delivery status timeline.
- [ ] No import/export.

## Phase 6
- [ ] No unit/integration/UI tests.
- [ ] No CI/CD, lint, or Crashlytics.
