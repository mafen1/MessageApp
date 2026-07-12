# MessageApp — Portfolio redesign: Phase 0–1 design

## Goal

Bring the existing Jetpack Compose messenger from a "learning project" state to a junior+/middle portfolio level by establishing a clean architecture baseline, solid dependency injection, and a documented technical debt map.

## Scope of Phase 0–1

Phase 0: audit current Android and server code, document baseline architecture and debt.  
Phase 1: refactor Android architecture to Clean Architecture + MVVM with proper Hilt modules and package boundaries. Keep the app compiling and functional after every change.

Out of scope for this phase: WebSocket state machine, protobuf, Room offline-first, foreground service, E2E, WebRTC, CI/CD. Those are Phase 2+.

## Current state (baseline)

### Android (`/Users/mafen/ww/programming/android/MessageApp`)

- **UI**: single `ComponentActivity` + Jetpack Compose + Compose Navigation with type-safe routes.
- **Theme**: Material 3, light/dark palettes in dark-blue tones (`ui/theme/Color.kt`, `Theme.kt`).
- **DI**: Hilt is already present; `Module.kt` wires Retrofit, OkHttp, Gson, `AppPreference`/`DataStore`.
- **Networking**: Retrofit + OkHttp + Gson; `AuthInterceptor` adds token.
- **WebSocket**: Java-WebSocket client exists in `data/network/webSocket/client/`.
- **Local storage**: DataStore for token/user; Room dependencies present but not used for messages.
- **Build**: `./gradlew :app:assembleDebug` succeeds.

### Problems found

1. **Domain layer is thin/wrong**: `ApiServiceUseCase` is a 1:1 pass-through to `ApiServiceImpl`. Use cases expose data/network DTOs (`LoginRequest`, `UserResponse`, etc.) instead of domain models.
2. **Repository abstraction leaks**: `ApiRepository` interface lives in `domain.repo.apiRepository`, but its implementation `ApiServiceImpl` is in `domain.repoImpl`. Domain depends on data details (Retrofit `MultipartBody`, Gson, `okhttp3.RequestBody`).
3. **ViewModels depend on implementation**: e.g. `RegisterViewModel` uses `ApiServiceImpl` directly (observed in imports).
4. **Package structure is inconsistent**: some screens under `ui/screen/`, some under `ui/chatListScreen/`, `ui/chatScreen/`, etc.
5. **Legacy XML still present**: `themes.xml`, `colors.xml`, `anim/anim.xml` are unused in a Compose-only app.
6. **Server**: Ktor + Koin + Exposed + PostgreSQL/H2 + JWT + WebSockets. DI and basic repo interfaces already exist. WebSocket manager is a simple in-memory map without reconnect/dedup logic.

## Target architecture (Android)

```
com.example.messageapp
├── app                     # Application class
├── core                    # Extensions, constants, UI state helpers, Result wrappers
├── data
│   ├── local               # Room (prepared in Phase 3)
│   ├── remote
│   │   ├── api             # Retrofit service + DTOs
│   │   └── socket          # WebSocket client(s)
│   ├── repository          # Repository implementations
│   └── model               # Data-layer DTOs
├── domain
│   ├── model               # Pure Kotlin domain models (no Android, no Retrofit)
│   ├── repository          # Repository interfaces
│   └── usecase             # One use case per operation
├── di                      # Hilt modules
└── ui
    ├── components          # Reusable Compose components
    ├── navigation          # Routes + NavHost
    ├── screen
    │   ├── auth
    │   ├── chat
    │   ├── chatlist
    │   ├── news
    │   ├── onboarding
    │   ├── search
    │   └── welcome
    └── theme
```

### Dependency rule

- `domain` has no dependencies on Android, Retrofit, Gson, Room, DataStore.
- `data` depends on `domain` (implements repository interfaces) and remote/local sources.
- `ui` depends on `domain` and `di` only.
- Hilt modules live in `di` and wire concrete implementations to interfaces.

## Concrete changes for Phase 1

### 1. Reorganize packages

Move all feature screens into `ui.screen.<feature>` using lowercase feature names:
- `ui/chatListScreen/` → `ui/screen/chatlist/`
- `ui/chatScreen/` → `ui/screen/chat/`
- `ui/newsListScreen/` → `ui/screen/news/`
- `ui/addNewsScreen/` → `ui/screen/news/` (merged with CreatePost)
- `ui/registerScreen/` → `ui/screen/auth/`
- `ui/listUserScreen/` → `ui/screen/search/`
- `ui/splashScreen/` → `ui/screen/welcome/`

### 2. Remove unused XML resources

Delete:
- `res/values-night/themes.xml` and `res/values/themes.xml` (Compose sets status bar in `Theme.kt`).
- `res/values-night/colors.xml` and `res/values/colors.xml`.
- `res/anim/anim.xml` if unused.

Keep:
- `strings.xml` for localization.
- `drawable/ic_launcher_*` and `xml/*` (security/network config).

### 3. Introduce domain models

Create pure domain models in `domain.model`:
- `User`, `UserCredentials`, `Message`, `Chat`, `NewsPost`, `Comment`, `Like`, `FriendRequest`.

They must not reference any data-layer class.

### 4. Refactor repository layer

- Move `ApiRepository` interface to `domain.repository` and rename to a feature-specific contract, e.g. `UserRepository`, `MessageRepository`, `NewsRepository`, `AuthRepository`.
- Keep `ApiServiceImpl` in `data.repository` and make it implement the new interfaces.
- Remove `okhttp3.MultipartBody` and `RequestBody` from repository interfaces; expose `ByteArray`/URI/FILE + domain models, and let the implementation build multipart bodies internally.

### 5. Refactor use cases

Replace the single god `ApiServiceUseCase` with focused use cases:
- `LoginUseCase`
- `RegisterUseCase`
- `GetCurrentUserUseCase`
- `SearchUsersUseCase`
- `GetFriendsUseCase`
- `SendFriendRequestUseCase`
- `GetChatHistoryUseCase`
- `SendMessageUseCase`
- `GetNewsFeedUseCase`
- `CreatePostUseCase`
- `LikePostUseCase`
- `CommentPostUseCase`

Each accepts domain models and returns `Result<DomainModel>` or `Flow<List<DomainModel>>`.

### 6. Update ViewModels and DI

- ViewModels depend on use cases, not repositories directly.
- Hilt modules:
  - `NetworkModule`: Retrofit, OkHttp, Gson, ApiService.
  - `DataStoreModule`: DataStore / AppPreference.
  - `RepositoryModule`: binds interfaces to implementations.
  - `UseCaseModule`: provides use cases.

### 7. Document baseline

Create/ update:
- `docs/ARCHITECTURE.md`: current layers, dependency rules, module map.
- `docs/DEBT.md`: list of known technical debt and planned phases.

## Success criteria

1. `./gradlew :app:assembleDebug` passes.
2. No `domain` class imports Retrofit/Gson/Room/DataStore/Compose.
3. All screens compile and navigation works as before.
4. Package tree follows `ui.screen.<feature>` convention.
5. `ARCHITECTURE.md` and `DEBT.md` exist and are accurate.

## Server changes in Phase 0–1 (minimal)

Server already has DI (Koin) and repository interfaces. For this phase:
- Document server architecture in `ServerMessage/docs/ARCHITECTURE.md`.
- Keep server API stable so Android refactor does not break it.
- Note future server work: protobuf binary framing, message deduplication (`clientMessageId`), delivery status persistence, typing events.

## Risks and mitigation

| Risk | Mitigation |
|------|-----------|
| Large rename breaks imports | Use IDE-safe moves; run build after each package move. |
| Repository signature changes break screens | Update DTO→domain mapping in repository impl first, then ViewModels. |
| Gson DTOs used directly in UI | Introduce domain models and mappers before deleting DTO usage. |
| Session time limit | Split Phase 1 into multiple sub-sessions; keep build green. |

## Next phases (high-level)

- Phase 2: WebSocket state machine on Android, protobuf framing on server, message deduplication.
- Phase 3: Room as source of truth, outbox pattern, WorkManager, FTS5 search.
- Phase 4: Foreground service, notifications with direct reply, media (voice/images), performance.
- Phase 5: E2E encryption, threads, delivery status, import/export.
- Phase 6: Tests, CI/CD, lint, Crashlytics.
- Phase 7: Portfolio presentation materials.
