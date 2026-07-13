# Technical Debt

## Phase 0–1 (resolved)
- [x] Migrated UI to Jetpack Compose with Material 3 dark-blue theme.
- [x] Established Clean Architecture layers (`domain`, `data`, `ui`).
- [x] Replaced legacy `ApiRepository` / `ApiServiceImpl` / `ApiServiceUseCase` with focused repositories and use cases.
- [x] Moved preferences implementation out of `domain` so the domain layer has no Android/DataStore imports.
- [x] Split Hilt modules into `NetworkModule`, `DataStoreModule`, `RepositoryModule`, `UseCaseModule`.
- [x] Removed unused XML layouts, fragments, adapters, menus, and navigation resources.

## Phase 2
- [ ] Android WebSocket client lacks state machine and reconnect logic.
- [ ] Server WebSocket manager has no deduplication or delivery receipts.
- [ ] No protobuf binary framing.

## Phase 3
- [ ] Room is not used as source of truth.
- [ ] No outbox pattern or WorkManager sync.
- [ ] No FTS search.

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
