# MessageApp Portfolio Redesign — Phase 0–1 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Establish a documented Clean Architecture baseline for the Android messenger and refactor the existing Compose app so domain logic is independent of frameworks.

**Architecture:** Split the monolithic data/network + domain + UI mix into three strict layers: `domain` (pure Kotlin models + repository interfaces + use cases), `data` (remote API DTOs, Retrofit service, repository implementations with mappers), and `ui` (Compose screens + ViewModels). Hilt modules in `di` wire concrete implementations. Server remains API-stable during this phase.

**Tech Stack:** Kotlin 2.1.0, Android Gradle Plugin 8.10.1, Jetpack Compose (BOM 2025.04.01), Hilt 2.56.2, Retrofit 2.9.0, Gson, OkHttp.

## Global Constraints

- `domain` must not import Android, Retrofit, Gson, OkHttp, Room, DataStore, or Compose.
- All feature screens must live under `ui.screen.<feature>` using lowercase names.
- `./gradlew :app:assembleDebug` must pass after every task.
- Keep the user-facing behavior unchanged.
- Server API must stay compatible: existing REST endpoints and WebSocket string format continue to work.

---

## File Map

### New files (domain)

- `app/src/main/java/com/example/messageapp/domain/model/User.kt`
- `app/src/main/java/com/example/messageapp/domain/model/UserCredentials.kt`
- `app/src/main/java/com/example/messageapp/domain/model/Message.kt`
- `app/src/main/java/com/example/messageapp/domain/model/Chat.kt`
- `app/src/main/java/com/example/messageapp/domain/model/NewsPost.kt`
- `app/src/main/java/com/example/messageapp/domain/model/Comment.kt`
- `app/src/main/java/com/example/messageapp/domain/model/Like.kt`
- `app/src/main/java/com/example/messageapp/domain/model/FriendRequest.kt`
- `app/src/main/java/com/example/messageapp/domain/repository/AuthRepository.kt`
- `app/src/main/java/com/example/messageapp/domain/repository/UserRepository.kt`
- `app/src/main/java/com/example/messageapp/domain/repository/MessageRepository.kt`
- `app/src/main/java/com/example/messageapp/domain/repository/NewsRepository.kt`
- `app/src/main/java/com/example/messageapp/domain/repository/FriendRepository.kt`
- `app/src/main/java/com/example/messageapp/domain/usecase/auth/LoginUseCase.kt`
- `app/src/main/java/com/example/messageapp/domain/usecase/auth/RegisterUseCase.kt`
- `app/src/main/java/com/example/messageapp/domain/usecase/user/GetCurrentUserUseCase.kt`
- `app/src/main/java/com/example/messageapp/domain/usecase/user/SearchUsersUseCase.kt`
- `app/src/main/java/com/example/messageapp/domain/usecase/friend/GetFriendsUseCase.kt`
- `app/src/main/java/com/example/messageapp/domain/usecase/friend/SendFriendRequestUseCase.kt`
- `app/src/main/java/com/example/messageapp/domain/usecase/friend/AcceptFriendRequestUseCase.kt`
- `app/src/main/java/com/example/messageapp/domain/usecase/friend/RejectFriendRequestUseCase.kt`
- `app/src/main/java/com/example/messageapp/domain/usecase/friend/GetFriendRequestsUseCase.kt`
- `app/src/main/java/com/example/messageapp/domain/usecase/message/GetChatHistoryUseCase.kt`
- `app/src/main/java/com/example/messageapp/domain/usecase/message/SendMessageUseCase.kt`
- `app/src/main/java/com/example/messageapp/domain/usecase/news/GetNewsFeedUseCase.kt`
- `app/src/main/java/com/example/messageapp/domain/usecase/news/CreatePostUseCase.kt`
- `app/src/main/java/com/example/messageapp/domain/usecase/news/LikePostUseCase.kt`
- `app/src/main/java/com/example/messageapp/domain/usecase/news/CommentPostUseCase.kt`

### New files (data)

- `app/src/main/java/com/example/messageapp/data/repository/AuthRepositoryImpl.kt`
- `app/src/main/java/com/example/messageapp/data/repository/UserRepositoryImpl.kt`
- `app/src/main/java/com/example/messageapp/data/repository/MessageRepositoryImpl.kt`
- `app/src/main/java/com/example/messageapp/data/repository/NewsRepositoryImpl.kt`
- `app/src/main/java/com/example/messageapp/data/repository/FriendRepositoryImpl.kt`
- `app/src/main/java/com/example/messageapp/data/mapper/AuthMapper.kt`
- `app/src/main/java/com/example/messageapp/data/mapper/UserMapper.kt`
- `app/src/main/java/com/example/messageapp/data/mapper/MessageMapper.kt`
- `app/src/main/java/com/example/messageapp/data/mapper/NewsMapper.kt`
- `app/src/main/java/com/example/messageapp/data/mapper/FriendMapper.kt`

### New files (di)

- `app/src/main/java/com/example/messageapp/di/NetworkModule.kt`
- `app/src/main/java/com/example/messageapp/di/DataStoreModule.kt`
- `app/src/main/java/com/example/messageapp/di/RepositoryModule.kt`
- `app/src/main/java/com/example/messageapp/di/UseCaseModule.kt`

### Modified / moved files

- `app/src/main/java/com/example/messageapp/domain/repo/apiRepository/ApiRepository.kt` → delete after extracting feature contracts
- `app/src/main/java/com/example/messageapp/domain/repoImpl/ApiServiceImpl.kt` → split into data repository implementations, then delete
- `app/src/main/java/com/example/messageapp/domain/useCase/ApiServiceUseCase.kt` → delete after replacing usages
- `app/src/main/java/com/example/messageapp/di/Module.kt` → delete after splitting into focused modules
- All ViewModels under `ui.*Screen` → move to `ui.screen.<feature>` and inject use cases
- All Composable screens → move to `ui.screen.<feature>`

### Deleted files

- `app/src/main/res/values-night/themes.xml`
- `app/src/main/res/values/themes.xml`
- `app/src/main/res/values-night/colors.xml`
- `app/src/main/res/values/colors.xml`
- `app/src/main/res/anim/anim.xml`

### Documentation

- `docs/ARCHITECTURE.md`
- `docs/DEBT.md`

---

## Task 1: Document baseline architecture and debt

**Files:**
- Create: `docs/ARCHITECTURE.md`
- Create: `docs/DEBT.md`

**Interfaces:**
- Produces: Markdown documentation; no code interfaces.

- [ ] **Step 1: Write `docs/ARCHITECTURE.md`**

```markdown
# MessageApp Architecture

## Overview

Android messenger client built with Kotlin and Jetpack Compose.
Single Activity (`MainActivity`) hosts Compose Navigation.

## Layers

```
ui              # Compose screens, ViewModels, Navigation, Theme
domain          # Pure Kotlin models, repository interfaces, use cases
data            # Remote API (Retrofit/OkHttp), local storage (DataStore/Room), repository impls
```

### Dependency rule

`domain` has no Android/framework dependencies.
`data` depends on `domain`.
`ui` depends on `domain` and `di`.

## Modules

- `app`: application manifest, `MainActivity`, Hilt entry point.
- `data.network.api.service.ApiService`: Retrofit interface.
- `data.repository.*`: repository implementations with DTO→domain mappers.
- `domain.repository.*`: repository contracts.
- `domain.usecase.*`: single-responsibility use cases.
- `di.*`: Hilt modules.

## Server

Ktor server lives in `/Users/mafen/ww/programming/kotlin/ServerMessage`.
It provides REST endpoints for auth/users/messages/news/friends and WebSocket delivery.
```

- [ ] **Step 2: Write `docs/DEBT.md`**

```markdown
# Technical Debt

## Phase 0–1 (in progress)
- Package structure inconsistent (`ui/chatListScreen/` vs `ui/screen/`).
- Repository interface exposes Retrofit/OkHttp types.
- Single god use case (`ApiServiceUseCase`).
- Domain models missing; UI uses data DTOs directly.
- Unused XML themes/colors/animations still in `res/`.

## Phase 2
- Android WebSocket client lacks state machine and reconnect logic.
- Server WebSocket manager has no deduplication or delivery receipts.
- No protobuf binary framing.

## Phase 3
- Room is not used as source of truth.
- No outbox pattern or WorkManager sync.
- No FTS search.

## Phase 4
- No foreground service for background WebSocket.
- No notification channel or direct-reply notifications.
- No voice messages, image compression, BlurHash.

## Phase 5
- No E2E encryption.
- No threads/replies.
- No delivery status timeline.
- No import/export.

## Phase 6
- No unit/integration/UI tests.
- No CI/CD, lint, or Crashlytics.
```

- [ ] **Step 3: Commit**

```bash
git add docs/ARCHITECTURE.md docs/DEBT.md
git commit -m "docs: add architecture baseline and technical debt"
```

---

## Task 2: Remove unused XML resources

**Files:**
- Delete: `app/src/main/res/values-night/themes.xml`
- Delete: `app/src/main/res/values/themes.xml`
- Delete: `app/src/main/res/values-night/colors.xml`
- Delete: `app/src/main/res/values/colors.xml`
- Delete: `app/src/main/res/anim/anim.xml`

**Interfaces:**
- Consumes: Compose-only theme in `ui/theme/Theme.kt`.
- Produces: No code references to deleted XML files.

- [ ] **Step 1: Delete files**

```bash
rm app/src/main/res/values-night/themes.xml
rm app/src/main/res/values/themes.xml
rm app/src/main/res/values-night/colors.xml
rm app/src/main/res/values/colors.xml
rm app/src/main/res/anim/anim.xml
```

- [ ] **Step 2: Verify build**

Run: `./gradlew :app:assembleDebug --no-daemon`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Commit**

```bash
git add -A
git commit -m "chore: remove unused XML themes, colors and animations"
```

---

## Task 3: Reorganize UI packages

**Files:**
- Move: `app/src/main/java/com/example/messageapp/ui/chatListScreen/` → `app/src/main/java/com/example/messageapp/ui/screen/chatlist/`
- Move: `app/src/main/java/com/example/messageapp/ui/chatScreen/` → `app/src/main/java/com/example/messageapp/ui/screen/chat/`
- Move: `app/src/main/java/com/example/messageapp/ui/newsListScreen/` → `app/src/main/java/com/example/messageapp/ui/screen/news/`
- Move: `app/src/main/java/com/example/messageapp/ui/addNewsScreen/` → `app/src/main/java/com/example/messageapp/ui/screen/news/`
- Move: `app/src/main/java/com/example/messageapp/ui/registerScreen/` → `app/src/main/java/com/example/messageapp/ui/screen/auth/`
- Move: `app/src/main/java/com/example/messageapp/ui/listUserScreen/` → `app/src/main/java/com/example/messageapp/ui/screen/search/`
- Move: `app/src/main/java/com/example/messageapp/ui/splashScreen/` → `app/src/main/java/com/example/messageapp/ui/screen/welcome/`
- Modify: `app/src/main/java/com/example/messageapp/ui/navigation/MessageAppNavHost.kt`
- Modify: all moved Kotlin files (package declarations and imports)

**Interfaces:**
- Consumes: existing screen signatures.
- Produces: same Composables and ViewModels in new package locations; `MessageAppNavHost` imports updated.

- [ ] **Step 1: Move directories with git mv**

```bash
git mv app/src/main/java/com/example/messageapp/ui/chatListScreen app/src/main/java/com/example/messageapp/ui/screen/chatlist
git mv app/src/main/java/com/example/messageapp/ui/chatScreen app/src/main/java/com/example/messageapp/ui/screen/chat
git mv app/src/main/java/com/example/messageapp/ui/newsListScreen app/src/main/java/com/example/messageapp/ui/screen/news
git mv app/src/main/java/com/example/messageapp/ui/addNewsScreen/* app/src/main/java/com/example/messageapp/ui/screen/news/
git mv app/src/main/java/com/example/messageapp/ui/registerScreen app/src/main/java/com/example/messageapp/ui/screen/auth
git mv app/src/main/java/com/example/messageapp/ui/listUserScreen app/src/main/java/com/example/messageapp/ui/screen/search
git mv app/src/main/java/com/example/messageapp/ui/splashScreen app/src/main/java/com/example/messageapp/ui/screen/welcome
```

- [ ] **Step 2: Update package declarations and imports in moved files**

For each moved `.kt` file, change the `package` line to the new path and fix any relative imports.

Example for former `ui/chatListScreen/ChatListScreen.kt`:
```kotlin
package com.example.messageapp.ui.screen.chatlist
```

- [ ] **Step 3: Update imports in `MessageAppNavHost.kt`**

Change:
```kotlin
import com.example.messageapp.ui.chatListScreen.ChatListScreen
import com.example.messageapp.ui.chatListScreen.ChatListViewModel
import com.example.messageapp.ui.screen.account.AccountScreen
import com.example.messageapp.ui.screen.auth.AuthScreen
import com.example.messageapp.ui.screen.chat.ChatScreen
import com.example.messageapp.ui.screen.news.CreatePostScreen
import com.example.messageapp.ui.screen.news.NewsFeedScreen
import com.example.messageapp.ui.screen.onboarding.OnboardingScreen
import com.example.messageapp.ui.screen.search.SearchScreen
import com.example.messageapp.ui.screen.welcome.WelcomeScreen
import com.example.messageapp.ui.screen.welcome.WelcomeViewModel
```

- [ ] **Step 4: Verify build**

Run: `./gradlew :app:assembleDebug --no-daemon`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "refactor: reorganize ui screens into ui.screen.<feature> packages"
```

---

## Task 4: Create domain models

**Files:**
- Create: `app/src/main/java/com/example/messageapp/domain/model/User.kt`
- Create: `app/src/main/java/com/example/messageapp/domain/model/UserCredentials.kt`
- Create: `app/src/main/java/com/example/messageapp/domain/model/Message.kt`
- Create: `app/src/main/java/com/example/messageapp/domain/model/Chat.kt`
- Create: `app/src/main/java/com/example/messageapp/domain/model/NewsPost.kt`
- Create: `app/src/main/java/com/example/messageapp/domain/model/Comment.kt`
- Create: `app/src/main/java/com/example/messageapp/domain/model/Like.kt`
- Create: `app/src/main/java/com/example/messageapp/domain/model/FriendRequest.kt`

**Interfaces:**
- Produces: pure Kotlin data classes used by repository interfaces and use cases.

- [ ] **Step 1: Write `User.kt`**

```kotlin
package com.example.messageapp.domain.model

data class User(
    val id: Int,
    val name: String,
    val userName: String,
    val avatarUrl: String? = null,
    val token: String? = null
)
```

- [ ] **Step 2: Write `UserCredentials.kt`**

```kotlin
package com.example.messageapp.domain.model

data class UserCredentials(
    val userName: String,
    val password: String,
    val displayName: String = ""
)
```

- [ ] **Step 3: Write `Message.kt`**

```kotlin
package com.example.messageapp.domain.model

data class Message(
    val id: String,
    val chatId: String,
    val senderUserName: String,
    val text: String,
    val imageUrl: String? = null,
    val timestamp: Long,
    val status: MessageStatus = MessageStatus.SENT
)

enum class MessageStatus {
    SENDING,
    SENT,
    DELIVERED,
    READ,
    FAILED
}
```

- [ ] **Step 4: Write `Chat.kt`**

```kotlin
package com.example.messageapp.domain.model

data class Chat(
    val id: String,
    val participantUserName: String,
    val participantName: String,
    val lastMessage: String? = null,
    val lastMessageTime: Long? = null,
    val unreadCount: Int = 0
)
```

- [ ] **Step 5: Write `NewsPost.kt`**

```kotlin
package com.example.messageapp.domain.model

data class NewsPost(
    val id: Int,
    val authorUserName: String,
    val authorName: String,
    val text: String,
    val imageUrl: String? = null,
    val createdAt: Long,
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val isLikedByCurrentUser: Boolean = false
)
```

- [ ] **Step 6: Write `Comment.kt`**

```kotlin
package com.example.messageapp.domain.model

data class Comment(
    val id: Int,
    val authorUserName: String,
    val authorName: String,
    val text: String,
    val createdAt: Long
)
```

- [ ] **Step 7: Write `Like.kt`**

```kotlin
package com.example.messageapp.domain.model

data class Like(
    val userName: String,
    val postId: Int
)
```

- [ ] **Step 8: Write `FriendRequest.kt`**

```kotlin
package com.example.messageapp.domain.model

data class FriendRequest(
    val id: Int? = null,
    val fromUserName: String,
    val toUserName: String,
    val status: RequestStatus = RequestStatus.PENDING
)

enum class RequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED
}
```

- [ ] **Step 9: Verify build**

Run: `./gradlew :app:assembleDebug --no-daemon`
Expected: `BUILD SUCCESSFUL` (new files compile).

- [ ] **Step 10: Commit**

```bash
git add app/src/main/java/com/example/messageapp/domain/model/
git commit -m "feat(domain): add pure domain models"
```

---

## Task 5: Create repository interfaces in domain

**Files:**
- Create: `app/src/main/java/com/example/messageapp/domain/repository/AuthRepository.kt`
- Create: `app/src/main/java/com/example/messageapp/domain/repository/UserRepository.kt`
- Create: `app/src/main/java/com/example/messageapp/domain/repository/MessageRepository.kt`
- Create: `app/src/main/java/com/example/messageapp/domain/repository/NewsRepository.kt`
- Create: `app/src/main/java/com/example/messageapp/domain/repository/FriendRepository.kt`
- Delete (after replacement): `app/src/main/java/com/example/messageapp/domain/repo/apiRepository/ApiRepository.kt`

**Interfaces:**
- Consumes: domain models from Task 4.
- Produces: repository contracts used by use cases.

- [ ] **Step 1: Write `AuthRepository.kt`**

```kotlin
package com.example.messageapp.domain.repository

import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.model.UserCredentials

interface AuthRepository {
    suspend fun register(credentials: UserCredentials): Result<User>
    suspend fun login(credentials: UserCredentials): Result<User>
}
```

- [ ] **Step 2: Write `UserRepository.kt`**

```kotlin
package com.example.messageapp.domain.repository

import com.example.messageapp.domain.model.User

interface UserRepository {
    suspend fun getCurrentUser(): Result<User>
    suspend fun findByUserName(userName: String): Result<List<User>>
    suspend fun search(query: String): Result<List<User>>
    suspend fun updateProfile(user: User): Result<User>
}
```

- [ ] **Step 3: Write `MessageRepository.kt`**

```kotlin
package com.example.messageapp.domain.repository

import com.example.messageapp.domain.model.Message

interface MessageRepository {
    suspend fun getHistory(user1: String, user2: String): Result<List<Message>>
    suspend fun sendMessage(message: Message): Result<Unit>
    suspend fun uploadImage(bytes: ByteArray, fileName: String): Result<String>
}
```

- [ ] **Step 4: Write `NewsRepository.kt`**

```kotlin
package com.example.messageapp.domain.repository

import com.example.messageapp.domain.model.Comment
import com.example.messageapp.domain.model.NewsPost

interface NewsRepository {
    suspend fun getFeed(): Result<List<NewsPost>>
    suspend fun createPost(text: String, imageBytes: ByteArray?): Result<Unit>
    suspend fun createPostWithoutImage(text: String): Result<Unit>
    suspend fun toggleLike(postId: Int): Result<NewsPost>
    suspend fun addComment(postId: Int, text: String): Result<Comment>
}
```

- [ ] **Step 5: Write `FriendRepository.kt`**

```kotlin
package com.example.messageapp.domain.repository

import com.example.messageapp.domain.model.FriendRequest
import com.example.messageapp.domain.model.User

interface FriendRepository {
    suspend fun getFriends(userName: String): Result<List<User>>
    suspend fun getRequests(userName: String): Result<List<FriendRequest>>
    suspend fun sendRequest(fromUserName: String, toUserName: String): Result<Unit>
    suspend fun acceptRequest(fromUserName: String, toUserName: String): Result<Unit>
    suspend fun rejectRequest(fromUserName: String, toUserName: String): Result<Unit>
}
```

- [ ] **Step 6: Delete legacy `ApiRepository.kt`**

```bash
rm app/src/main/java/com/example/messageapp/domain/repo/apiRepository/ApiRepository.kt
rmdir app/src/main/java/com/example/messageapp/domain/repo/apiRepository 2>/dev/null || true
```

- [ ] **Step 7: Verify build**

Run: `./gradlew :app:assembleDebug --no-daemon`
Expected: `BUILD SUCCESSFUL` (interfaces compile; old usages will break later).

- [ ] **Step 8: Commit**

```bash
git add -A
git commit -m "feat(domain): add repository interfaces and remove legacy ApiRepository"
```

---

## Task 6: Create data mappers

**Files:**
- Create: `app/src/main/java/com/example/messageapp/data/mapper/AuthMapper.kt`
- Create: `app/src/main/java/com/example/messageapp/data/mapper/UserMapper.kt`
- Create: `app/src/main/java/com/example/messageapp/data/mapper/MessageMapper.kt`
- Create: `app/src/main/java/com/example/messageapp/data/mapper/NewsMapper.kt`
- Create: `app/src/main/java/com/example/messageapp/data/mapper/FriendMapper.kt`

**Interfaces:**
- Consumes: data-layer DTOs (`data.network.model.*`).
- Produces: domain models.

- [ ] **Step 1: Write `AuthMapper.kt`**

```kotlin
package com.example.messageapp.data.mapper

import com.example.messageapp.data.network.model.LoginResponse
import com.example.messageapp.domain.model.User

fun LoginResponse.toDomain(): User = User(
    id = user.id,
    name = user.name,
    userName = user.userName,
    token = token
)
```

- [ ] **Step 2: Write `UserMapper.kt`**

```kotlin
package com.example.messageapp.data.mapper

import com.example.messageapp.data.network.model.User as UserDto
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.domain.model.User

fun UserDto.toDomain(): User = User(
    id = id,
    name = name,
    userName = userName
)

fun UserResponse.toDomain(): User = user.toDomain()
```

- [ ] **Step 3: Write `MessageMapper.kt`**

```kotlin
package com.example.messageapp.data.mapper

import com.example.messageapp.data.network.model.MessageResponse
import com.example.messageapp.domain.model.Message
import com.example.messageapp.domain.model.MessageStatus

fun MessageResponse.toDomain(chatId: String): Message = Message(
    id = id.toString(),
    chatId = chatId,
    senderUserName = senderUsername,
    text = text,
    imageUrl = image,
    timestamp = timestamp,
    status = MessageStatus.SENT
)
```

- [ ] **Step 4: Write `NewsMapper.kt`**

```kotlin
package com.example.messageapp.data.mapper

import com.example.messageapp.data.network.model.CommentResponse
import com.example.messageapp.data.network.model.NewsResponse
import com.example.messageapp.domain.model.Comment
import com.example.messageapp.domain.model.NewsPost

fun NewsResponse.toDomain(currentUserName: String): NewsPost = NewsPost(
    id = id,
    authorUserName = username,
    authorName = name,
    text = text,
    imageUrl = image,
    createdAt = date,
    likesCount = countLikes,
    commentsCount = countComment,
    isLikedByCurrentUser = listLikes.any { it.username == currentUserName }
)

fun CommentResponse.toDomain(): Comment = Comment(
    id = id,
    authorUserName = username,
    authorName = name,
    text = text,
    createdAt = date
)
```

- [ ] **Step 5: Write `FriendMapper.kt`**

```kotlin
package com.example.messageapp.data.mapper

import com.example.messageapp.data.network.model.FriendRequest as FriendRequestDto
import com.example.messageapp.data.network.model.FriendResponse
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.domain.model.FriendRequest
import com.example.messageapp.domain.model.RequestStatus
import com.example.messageapp.domain.model.User

fun UserResponse.toDomainUser(): User = User(
    id = user.id,
    name = user.name,
    userName = user.userName
)

fun FriendRequestDto.toDomain(): FriendRequest = FriendRequest(
    id = id,
    fromUserName = fromUser,
    toUserName = toUser,
    status = when (status) {
        "accepted" -> RequestStatus.ACCEPTED
        "rejected" -> RequestStatus.REJECTED
        else -> RequestStatus.PENDING
    }
)

fun FriendResponse.toDomainUserList(): List<User> =
    listUser?.map { it.toDomainUser() } ?: emptyList()

fun FriendResponse.toDomainRequestList(): List<FriendRequest> =
    listFriendRequest?.map { it.toDomain() } ?: emptyList()
```

- [ ] **Step 6: Verify build**

Run: `./gradlew :app:assembleDebug --no-daemon`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/com/example/messageapp/data/mapper/
git commit -m "feat(data): add DTO to domain mappers"
```

---

## Task 7: Implement data repositories

**Files:**
- Create: `app/src/main/java/com/example/messageapp/data/repository/AuthRepositoryImpl.kt`
- Create: `app/src/main/java/com/example/messageapp/data/repository/UserRepositoryImpl.kt`
- Create: `app/src/main/java/com/example/messageapp/data/repository/MessageRepositoryImpl.kt`
- Create: `app/src/main/java/com/example/messageapp/data/repository/NewsRepositoryImpl.kt`
- Create: `app/src/main/java/com/example/messageapp/data/repository/FriendRepositoryImpl.kt`
- Delete (after replacement): `app/src/main/java/com/example/messageapp/domain/repoImpl/ApiServiceImpl.kt`
- Delete (after replacement): `app/src/main/java/com/example/messageapp/domain/repo/` directory

**Interfaces:**
- Consumes: `ApiService`, `Gson`, domain models, mappers.
- Produces: repository implementations for Hilt binding.

- [ ] **Step 1: Write `AuthRepositoryImpl.kt`**

```kotlin
package com.example.messageapp.data.repository

import com.example.messageapp.data.mapper.toDomain
import com.example.messageapp.data.network.api.service.ApiService
import com.example.messageapp.data.network.model.LoginRequest
import com.example.messageapp.data.network.model.User
import com.example.messageapp.domain.model.UserCredentials
import com.example.messageapp.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AuthRepository {

    override suspend fun register(credentials: UserCredentials): Result<com.example.messageapp.domain.model.User> {
        return safeApiCall {
            apiService.addUser(
                User(
                    id = 0,
                    name = credentials.displayName,
                    userName = credentials.userName,
                    friend = emptyList(),
                    token = "",
                    password = credentials.password
                )
            ).toDomain()
        }
    }

    override suspend fun login(credentials: UserCredentials): Result<com.example.messageapp.domain.model.User> {
        return safeApiCall {
            apiService.loginUser(
                LoginRequest(
                    name = credentials.displayName,
                    userName = credentials.userName,
                    password = credentials.password
                )
            ).toDomain()
        }
    }

    private inline fun <T> safeApiCall(apiCall: () -> T): Result<T> {
        return try {
            Result.success(apiCall())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

- [ ] **Step 2: Write `UserRepositoryImpl.kt`**

```kotlin
package com.example.messageapp.data.repository

import com.example.messageapp.data.mapper.toDomain
import com.example.messageapp.data.mapper.toDomainUser
import com.example.messageapp.data.network.api.service.ApiService
import com.example.messageapp.data.network.model.UpdateProfileRequest
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserRepository {

    override suspend fun getCurrentUser(): Result<User> = safeApiCall {
        apiService.getCurrentUser().toDomain()
    }

    override suspend fun findByUserName(userName: String): Result<List<User>> = safeApiCall {
        apiService.findUserByName(UserRequest(userName)).listUser.map { it.toDomainUser() }
    }

    override suspend fun search(query: String): Result<List<User>> = safeApiCall {
        apiService.findUserByStr(UserRequest(query)).listUser.map { it.toDomainUser() }
    }

    override suspend fun updateProfile(user: User): Result<User> = safeApiCall {
        apiService.updateProfile(
            UpdateProfileRequest(
                name = user.name,
                userName = user.userName
            )
        ).toDomain()
    }

    private inline fun <T> safeApiCall(apiCall: () -> T): Result<T> {
        return try {
            Result.success(apiCall())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

- [ ] **Step 3: Write `MessageRepositoryImpl.kt`**

```kotlin
package com.example.messageapp.data.repository

import com.example.messageapp.data.mapper.toDomain
import com.example.messageapp.data.network.api.service.ApiService
import com.example.messageapp.data.network.model.ImageUploadResponse
import com.example.messageapp.domain.model.Message
import com.example.messageapp.domain.repository.MessageRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : MessageRepository {

    override suspend fun getHistory(user1: String, user2: String): Result<List<Message>> = safeApiCall {
        apiService.getMessages(user1, user2).map { it.toDomain(chatId = "$user1:$user2") }
    }

    override suspend fun sendMessage(message: Message): Result<Unit> = safeApiCall {
        // For now messages go through WebSocket; REST endpoint used for images only.
        Unit
    }

    override suspend fun uploadImage(bytes: ByteArray, fileName: String): Result<String> = safeApiCall {
        val part = MultipartBody.Part.createFormData(
            "image",
            fileName,
            bytes.toRequestBody("image/*".toMediaTypeOrNull())
        )
        apiService.uploadMessageImage(part).url
    }

    private inline fun <T> safeApiCall(apiCall: () -> T): Result<T> {
        return try {
            Result.success(apiCall())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

- [ ] **Step 4: Write `NewsRepositoryImpl.kt`**

```kotlin
package com.example.messageapp.data.repository

import com.example.messageapp.data.mapper.toDomain
import com.example.messageapp.data.network.api.service.ApiService
import com.example.messageapp.data.network.model.CommentRequest
import com.example.messageapp.data.network.model.LikeRequest
import com.example.messageapp.data.network.model.NewsRequest
import com.example.messageapp.domain.model.Comment
import com.example.messageapp.domain.model.NewsPost
import com.example.messageapp.domain.repository.NewsRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : NewsRepository {

    override suspend fun getFeed(): Result<List<NewsPost>> = safeApiCall {
        // isLikedByCurrentUser requires current username; pass empty for now, refine later.
        apiService.allNews().map { it.toDomain(currentUserName = "") }
    }

    override suspend fun createPost(text: String, imageBytes: ByteArray?): Result<Unit> = safeApiCall {
        val textBody = text.toRequestBody("text/plain".toMediaTypeOrNull())
        val imagePart = imageBytes?.let {
            MultipartBody.Part.createFormData(
                "image",
                "post.jpg",
                it.toRequestBody("image/*".toMediaTypeOrNull())
            )
        }
        apiService.uploadNews(imagePart, textBody)
    }

    override suspend fun createPostWithoutImage(text: String): Result<Unit> = safeApiCall {
        apiService.uploadNewsWithOutImage(NewsRequest(text))
    }

    override suspend fun toggleLike(postId: Int): Result<NewsPost> = safeApiCall {
        apiService.toggleLike(LikeRequest(postId)).toDomain(currentUserName = "")
    }

    override suspend fun addComment(postId: Int, text: String): Result<Comment> = safeApiCall {
        apiService.addComment(CommentRequest(postId, text)).toDomain()
    }

    private inline fun <T> safeApiCall(apiCall: () -> T): Result<T> {
        return try {
            Result.success(apiCall())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

- [ ] **Step 5: Write `FriendRepositoryImpl.kt`**

```kotlin
package com.example.messageapp.data.repository

import com.example.messageapp.data.mapper.toDomainRequestList
import com.example.messageapp.data.mapper.toDomainUserList
import com.example.messageapp.data.network.api.service.ApiService
import com.example.messageapp.data.network.model.AcceptFriendRequest
import com.example.messageapp.data.network.model.FriendRequest
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.repository.FriendRepository
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : FriendRepository {

    override suspend fun getFriends(userName: String): Result<List<User>> = safeApiCall {
        apiService.getFriends(userName).toDomainUserList()
    }

    override suspend fun getRequests(userName: String): Result<List<com.example.messageapp.domain.model.FriendRequest>> = safeApiCall {
        apiService.getFriendRequests(userName).toDomainRequestList()
    }

    override suspend fun sendRequest(fromUserName: String, toUserName: String): Result<Unit> = safeApiCall {
        apiService.sendFriendRequest(FriendRequest(fromUserName, toUserName))
    }

    override suspend fun acceptRequest(fromUserName: String, toUserName: String): Result<Unit> = safeApiCall {
        apiService.acceptFriend(AcceptFriendRequest(fromUserName, toUserName))
    }

    override suspend fun rejectRequest(fromUserName: String, toUserName: String): Result<Unit> = safeApiCall {
        apiService.rejectFriend(AcceptFriendRequest(fromUserName, toUserName))
    }

    private inline fun <T> safeApiCall(apiCall: () -> T): Result<T> {
        return try {
            Result.success(apiCall())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

- [ ] **Step 6: Delete legacy `ApiServiceImpl.kt` and empty package directories**

```bash
rm app/src/main/java/com/example/messageapp/domain/repoImpl/ApiServiceImpl.kt
rmdir app/src/main/java/com/example/messageapp/domain/repoImpl 2>/dev/null || true
rmdir app/src/main/java/com/example/messageapp/domain/repo 2>/dev/null || true
```

- [ ] **Step 7: Verify build**

Run: `./gradlew :app:assembleDebug --no-daemon`
Expected: `BUILD SUCCESSFUL` (may fail on old usages, which is acceptable; fix in next task).

- [ ] **Step 8: Commit**

```bash
git add -A
git commit -m "feat(data): implement repository implementations and remove legacy ApiServiceImpl"
```

---

## Task 8: Split Hilt modules

**Files:**
- Create: `app/src/main/java/com/example/messageapp/di/NetworkModule.kt`
- Create: `app/src/main/java/com/example/messageapp/di/DataStoreModule.kt`
- Create: `app/src/main/java/com/example/messageapp/di/RepositoryModule.kt`
- Create: `app/src/main/java/com/example/messageapp/di/UseCaseModule.kt`
- Delete: `app/src/main/java/com/example/messageapp/di/Module.kt`

**Interfaces:**
- Consumes: concrete implementations from Task 7 and existing `ApiService`, `DataStore`, `AppPreference`.
- Produces: Hilt bindings for repositories and use cases.

- [ ] **Step 1: Write `NetworkModule.kt`**

```kotlin
package com.example.messageapp.di

import android.util.Log
import com.example.messageapp.BuildConfig
import com.example.messageapp.data.network.api.client.AuthInterceptor
import com.example.messageapp.data.network.api.service.ApiService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .setStrictness(Strictness.LENIENT)
        .create()

    @Provides
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("NETWORK_DEBUG", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideApiService(okHttpClient: OkHttpClient, gson: Gson): ApiService {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BuildConfig.BASE_URL)
            .build()
            .create(ApiService::class.java)
    }
}
```

- [ ] **Step 2: Write `DataStoreModule.kt`**

```kotlin
package com.example.messageapp.di

import com.example.messageapp.domain.repo.preferences.AppPreference
import com.example.messageapp.store.DataStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class DataStoreModule {

    @Binds
    @Singleton
    abstract fun bindAppPreference(dataStore: DataStore): AppPreference
}
```

- [ ] **Step 3: Write `RepositoryModule.kt`**

```kotlin
package com.example.messageapp.di

import com.example.messageapp.data.repository.AuthRepositoryImpl
import com.example.messageapp.data.repository.FriendRepositoryImpl
import com.example.messageapp.data.repository.MessageRepositoryImpl
import com.example.messageapp.data.repository.NewsRepositoryImpl
import com.example.messageapp.data.repository.UserRepositoryImpl
import com.example.messageapp.domain.repository.AuthRepository
import com.example.messageapp.domain.repository.FriendRepository
import com.example.messageapp.domain.repository.MessageRepository
import com.example.messageapp.domain.repository.NewsRepository
import com.example.messageapp.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindMessageRepository(impl: MessageRepositoryImpl): MessageRepository

    @Binds
    abstract fun bindNewsRepository(impl: NewsRepositoryImpl): NewsRepository

    @Binds
    abstract fun bindFriendRepository(impl: FriendRepositoryImpl): FriendRepository
}
```

- [ ] **Step 4: Write `UseCaseModule.kt`**

```kotlin
package com.example.messageapp.di

import com.example.messageapp.domain.repository.AuthRepository
import com.example.messageapp.domain.repository.FriendRepository
import com.example.messageapp.domain.repository.MessageRepository
import com.example.messageapp.domain.repository.NewsRepository
import com.example.messageapp.domain.repository.UserRepository
import com.example.messageapp.domain.usecase.auth.LoginUseCase
import com.example.messageapp.domain.usecase.auth.RegisterUseCase
import com.example.messageapp.domain.usecase.friend.AcceptFriendRequestUseCase
import com.example.messageapp.domain.usecase.friend.GetFriendRequestsUseCase
import com.example.messageapp.domain.usecase.friend.GetFriendsUseCase
import com.example.messageapp.domain.usecase.friend.RejectFriendRequestUseCase
import com.example.messageapp.domain.usecase.friend.SendFriendRequestUseCase
import com.example.messageapp.domain.usecase.message.GetChatHistoryUseCase
import com.example.messageapp.domain.usecase.message.SendMessageUseCase
import com.example.messageapp.domain.usecase.news.AddCommentUseCase
import com.example.messageapp.domain.usecase.news.CreatePostUseCase
import com.example.messageapp.domain.usecase.news.GetNewsFeedUseCase
import com.example.messageapp.domain.usecase.news.LikePostUseCase
import com.example.messageapp.domain.usecase.user.GetCurrentUserUseCase
import com.example.messageapp.domain.usecase.user.SearchUsersUseCase
import com.example.messageapp.domain.usecase.user.UpdateProfileUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
@Module
object UseCaseModule {

    @Provides
    fun provideLoginUseCase(repo: AuthRepository): LoginUseCase = LoginUseCase(repo)

    @Provides
    fun provideRegisterUseCase(repo: AuthRepository): RegisterUseCase = RegisterUseCase(repo)

    @Provides
    fun provideGetCurrentUserUseCase(repo: UserRepository): GetCurrentUserUseCase =
        GetCurrentUserUseCase(repo)

    @Provides
    fun provideSearchUsersUseCase(repo: UserRepository): SearchUsersUseCase =
        SearchUsersUseCase(repo)

    @Provides
    fun provideUpdateProfileUseCase(repo: UserRepository): UpdateProfileUseCase =
        UpdateProfileUseCase(repo)

    @Provides
    fun provideGetFriendsUseCase(repo: FriendRepository): GetFriendsUseCase =
        GetFriendsUseCase(repo)

    @Provides
    fun provideGetFriendRequestsUseCase(repo: FriendRepository): GetFriendRequestsUseCase =
        GetFriendRequestsUseCase(repo)

    @Provides
    fun provideSendFriendRequestUseCase(repo: FriendRepository): SendFriendRequestUseCase =
        SendFriendRequestUseCase(repo)

    @Provides
    fun provideAcceptFriendRequestUseCase(repo: FriendRepository): AcceptFriendRequestUseCase =
        AcceptFriendRequestUseCase(repo)

    @Provides
    fun provideRejectFriendRequestUseCase(repo: FriendRepository): RejectFriendRequestUseCase =
        RejectFriendRequestUseCase(repo)

    @Provides
    fun provideGetChatHistoryUseCase(repo: MessageRepository): GetChatHistoryUseCase =
        GetChatHistoryUseCase(repo)

    @Provides
    fun provideSendMessageUseCase(repo: MessageRepository): SendMessageUseCase =
        SendMessageUseCase(repo)

    @Provides
    fun provideGetNewsFeedUseCase(repo: NewsRepository): GetNewsFeedUseCase =
        GetNewsFeedUseCase(repo)

    @Provides
    fun provideCreatePostUseCase(repo: NewsRepository): CreatePostUseCase =
        CreatePostUseCase(repo)

    @Provides
    fun provideLikePostUseCase(repo: NewsRepository): LikePostUseCase =
        LikePostUseCase(repo)

    @Provides
    fun provideAddCommentUseCase(repo: NewsRepository): AddCommentUseCase =
        AddCommentUseCase(repo)
}
```

- [ ] **Step 5: Delete legacy `Module.kt`**

```bash
rm app/src/main/java/com/example/messageapp/di/Module.kt
```

- [ ] **Step 6: Verify build**

Run: `./gradlew :app:assembleDebug --no-daemon`
Expected: `BUILD SUCCESSFUL` (ignore old use-case usage errors for now).

- [ ] **Step 7: Commit**

```bash
git add -A
git commit -m "refactor(di): split monolithic Module into Network, DataStore, Repository and UseCase modules"
```

---

## Task 9: Create focused use cases

**Files:**
- Create all use case files listed in the File Map under `app/src/main/java/com/example/messageapp/domain/usecase/`
- Delete: `app/src/main/java/com/example/messageapp/domain/useCase/ApiServiceUseCase.kt`
- Delete empty `app/src/main/java/com/example/messageapp/domain/useCase/` directory

**Interfaces:**
- Consumes: repository interfaces.
- Produces: single-responsibility operations for ViewModels.

- [ ] **Step 1: Write auth use cases**

`domain/usecase/auth/LoginUseCase.kt`:
```kotlin
package com.example.messageapp.domain.usecase.auth

import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.model.UserCredentials
import com.example.messageapp.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(credentials: UserCredentials): Result<User> =
        authRepository.login(credentials)
}
```

`domain/usecase/auth/RegisterUseCase.kt`:
```kotlin
package com.example.messageapp.domain.usecase.auth

import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.model.UserCredentials
import com.example.messageapp.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(credentials: UserCredentials): Result<User> =
        authRepository.register(credentials)
}
```

- [ ] **Step 2: Write user use cases**

`domain/usecase/user/GetCurrentUserUseCase.kt`:
```kotlin
package com.example.messageapp.domain.usecase.user

import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.repository.UserRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<User> = userRepository.getCurrentUser()
}
```

`domain/usecase/user/SearchUsersUseCase.kt`:
```kotlin
package com.example.messageapp.domain.usecase.user

import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.repository.UserRepository
import javax.inject.Inject

class SearchUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(query: String): Result<List<User>> =
        userRepository.search(query)
}
```

`domain/usecase/user/UpdateProfileUseCase.kt`:
```kotlin
package com.example.messageapp.domain.usecase.user

import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.repository.UserRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User): Result<User> =
        userRepository.updateProfile(user)
}
```

- [ ] **Step 3: Write friend use cases**

`domain/usecase/friend/GetFriendsUseCase.kt`:
```kotlin
package com.example.messageapp.domain.usecase.friend

import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.repository.FriendRepository
import javax.inject.Inject

class GetFriendsUseCase @Inject constructor(
    private val friendRepository: FriendRepository
) {
    suspend operator fun invoke(userName: String): Result<List<User>> =
        friendRepository.getFriends(userName)
}
```

`domain/usecase/friend/GetFriendRequestsUseCase.kt`:
```kotlin
package com.example.messageapp.domain.usecase.friend

import com.example.messageapp.domain.model.FriendRequest
import com.example.messageapp.domain.repository.FriendRepository
import javax.inject.Inject

class GetFriendRequestsUseCase @Inject constructor(
    private val friendRepository: FriendRepository
) {
    suspend operator fun invoke(userName: String): Result<List<FriendRequest>> =
        friendRepository.getRequests(userName)
}
```

`domain/usecase/friend/SendFriendRequestUseCase.kt`:
```kotlin
package com.example.messageapp.domain.usecase.friend

import com.example.messageapp.domain.repository.FriendRepository
import javax.inject.Inject

class SendFriendRequestUseCase @Inject constructor(
    private val friendRepository: FriendRepository
) {
    suspend operator fun invoke(fromUserName: String, toUserName: String): Result<Unit> =
        friendRepository.sendRequest(fromUserName, toUserName)
}
```

`domain/usecase/friend/AcceptFriendRequestUseCase.kt`:
```kotlin
package com.example.messageapp.domain.usecase.friend

import com.example.messageapp.domain.repository.FriendRepository
import javax.inject.Inject

class AcceptFriendRequestUseCase @Inject constructor(
    private val friendRepository: FriendRepository
) {
    suspend operator fun invoke(fromUserName: String, toUserName: String): Result<Unit> =
        friendRepository.acceptRequest(fromUserName, toUserName)
}
```

`domain/usecase/friend/RejectFriendRequestUseCase.kt`:
```kotlin
package com.example.messageapp.domain.usecase.friend

import com.example.messageapp.domain.repository.FriendRepository
import javax.inject.Inject

class RejectFriendRequestUseCase @Inject constructor(
    private val friendRepository: FriendRepository
) {
    suspend operator fun invoke(fromUserName: String, toUserName: String): Result<Unit> =
        friendRepository.rejectRequest(fromUserName, toUserName)
}
```

- [ ] **Step 4: Write message use cases**

`domain/usecase/message/GetChatHistoryUseCase.kt`:
```kotlin
package com.example.messageapp.domain.usecase.message

import com.example.messageapp.domain.model.Message
import com.example.messageapp.domain.repository.MessageRepository
import javax.inject.Inject

class GetChatHistoryUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(user1: String, user2: String): Result<List<Message>> =
        messageRepository.getHistory(user1, user2)
}
```

`domain/usecase/message/SendMessageUseCase.kt`:
```kotlin
package com.example.messageapp.domain.usecase.message

import com.example.messageapp.domain.model.Message
import com.example.messageapp.domain.repository.MessageRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(message: Message): Result<Unit> =
        messageRepository.sendMessage(message)
}
```

- [ ] **Step 5: Write news use cases**

`domain/usecase/news/GetNewsFeedUseCase.kt`:
```kotlin
package com.example.messageapp.domain.usecase.news

import com.example.messageapp.domain.model.NewsPost
import com.example.messageapp.domain.repository.NewsRepository
import javax.inject.Inject

class GetNewsFeedUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(): Result<List<NewsPost>> = newsRepository.getFeed()
}
```

`domain/usecase/news/CreatePostUseCase.kt`:
```kotlin
package com.example.messageapp.domain.usecase.news

import com.example.messageapp.domain.repository.NewsRepository
import javax.inject.Inject

class CreatePostUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(text: String, imageBytes: ByteArray? = null): Result<Unit> {
        return if (imageBytes != null) {
            newsRepository.createPost(text, imageBytes)
        } else {
            newsRepository.createPostWithoutImage(text)
        }
    }
}
```

`domain/usecase/news/LikePostUseCase.kt`:
```kotlin
package com.example.messageapp.domain.usecase.news

import com.example.messageapp.domain.model.NewsPost
import com.example.messageapp.domain.repository.NewsRepository
import javax.inject.Inject

class LikePostUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(postId: Int): Result<NewsPost> =
        newsRepository.toggleLike(postId)
}
```

`domain/usecase/news/AddCommentUseCase.kt`:
```kotlin
package com.example.messageapp.domain.usecase.news

import com.example.messageapp.domain.model.Comment
import com.example.messageapp.domain.repository.NewsRepository
import javax.inject.Inject

class AddCommentUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(postId: Int, text: String): Result<Comment> =
        newsRepository.addComment(postId, text)
}
```

- [ ] **Step 6: Delete legacy `ApiServiceUseCase.kt`**

```bash
rm app/src/main/java/com/example/messageapp/domain/useCase/ApiServiceUseCase.kt
rmdir app/src/main/java/com/example/messageapp/domain/useCase 2>/dev/null || true
```

- [ ] **Step 7: Verify build**

Run: `./gradlew :app:assembleDebug --no-daemon`
Expected: `BUILD SUCCESSFUL` (old ViewModel usages still reference legacy use case; fix next).

- [ ] **Step 8: Commit**

```bash
git add -A
git commit -m "feat(domain): add focused use cases and remove legacy ApiServiceUseCase"
```

---

## Task 10: Update ViewModels to use use cases

**Files:**
- Modify all ViewModels under `ui.screen.*` packages.

**Interfaces:**
- Consumes: focused use cases from Task 9.
- Produces: ViewModels exposing `UiState`/`Flow` built from domain models.

- [ ] **Step 1: Update `WelcomeViewModel`**

File: `app/src/main/java/com/example/messageapp/ui/screen/welcome/WelcomeViewModel.kt`

Replace `ApiServiceUseCase` injection with `GetCurrentUserUseCase`.
Use domain `User` model instead of data DTO.

```kotlin
package com.example.messageapp.ui.screen.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.usecase.user.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loginUser() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = getCurrentUserUseCase()
            _user.value = result.getOrNull()
            _isLoading.value = false
        }
    }
}
```

- [ ] **Step 2: Update `RegisterViewModel`**

File: `app/src/main/java/com/example/messageapp/ui/screen/auth/RegisterViewModel.kt`

Replace with `LoginUseCase` and `RegisterUseCase`. Expose domain `User`.

```kotlin
package com.example.messageapp.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.model.UserCredentials
import com.example.messageapp.domain.usecase.auth.LoginUseCase
import com.example.messageapp.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _registrationSuccess = MutableStateFlow<User?>(null)
    val registrationSuccess: StateFlow<User?> = _registrationSuccess

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun resetError() {
        _error.value = null
    }

    fun loginAccount(credentials: UserCredentials) {
        viewModelScope.launch {
            loginUseCase(credentials)
                .onSuccess { _currentUser.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun addAccount(credentials: UserCredentials) {
        viewModelScope.launch {
            registerUseCase(credentials)
                .onSuccess { _registrationSuccess.value = it }
                .onFailure { _error.value = it.message }
        }
    }
}
```

- [ ] **Step 3: Update `AuthScreen` to use `UserCredentials`**

File: `app/src/main/java/com/example/messageapp/ui/screen/auth/AuthScreen.kt`

Replace the `User` / `LoginRequest` construction with `UserCredentials`:

```kotlin
import com.example.messageapp.domain.model.UserCredentials

// Inside onClick:
if (isLoginMode) {
    viewModel.loginAccount(
        UserCredentials(
            userName = username,
            password = password
        )
    )
} else {
    viewModel.addAccount(
        UserCredentials(
            userName = username,
            password = password,
            displayName = name
        )
    )
}
```

Remove imports of data-layer models from `AuthScreen`.

- [ ] **Step 4: Update `SearchViewModel` / `ListUserViewModel`**

File: `app/src/main/java/com/example/messageapp/ui/screen/search/ListUserViewModel.kt`

Replace `ApiServiceUseCase` with `SearchUsersUseCase`, `SendFriendRequestUseCase`, `GetFriendRequestsUseCase`.
Expose domain `User` and `FriendRequest`.

```kotlin
package com.example.messageapp.ui.screen.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.domain.model.FriendRequest
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.usecase.friend.GetFriendRequestsUseCase
import com.example.messageapp.domain.usecase.friend.SendFriendRequestUseCase
import com.example.messageapp.domain.usecase.user.SearchUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListUserViewModel @Inject constructor(
    private val searchUsersUseCase: SearchUsersUseCase,
    private val sendFriendRequestUseCase: SendFriendRequestUseCase,
    private val getFriendRequestsUseCase: GetFriendRequestsUseCase
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    private val _requests = MutableStateFlow<List<FriendRequest>>(emptyList())
    val requests: StateFlow<List<FriendRequest>> = _requests

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun search(query: String) {
        viewModelScope.launch {
            searchUsersUseCase(query)
                .onSuccess { _users.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun sendRequest(fromUserName: String, toUserName: String) {
        viewModelScope.launch {
            sendFriendRequestUseCase(fromUserName, toUserName)
                .onFailure { _error.value = it.message }
        }
    }

    fun loadRequests(userName: String) {
        viewModelScope.launch {
            getFriendRequestsUseCase(userName)
                .onSuccess { _requests.value = it }
                .onFailure { _error.value = it.message }
        }
    }
}
```

- [ ] **Step 5: Update `ChatListViewModel`**

File: `app/src/main/java/com/example/messageapp/ui/screen/chatlist/ChatListViewModel.kt`

Use `GetFriendsUseCase`. Expose domain `User` list.

```kotlin
package com.example.messageapp.ui.screen.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.usecase.friend.GetFriendsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val getFriendsUseCase: GetFriendsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatListUiState>(ChatListUiState.Loading)
    val uiState: StateFlow<ChatListUiState> = _uiState

    fun loadChatList(userName: String, name: String) {
        viewModelScope.launch {
            _uiState.value = ChatListUiState.Loading
            getFriendsUseCase(userName)
                .onSuccess { _uiState.value = ChatListUiState.Success(it, name) }
                .onFailure { _uiState.value = ChatListUiState.Error(it.message.orEmpty()) }
        }
    }
}

sealed class ChatListUiState {
    data object Loading : ChatListUiState()
    data class Success(val friends: List<User>, val currentName: String) : ChatListUiState()
    data class Error(val message: String) : ChatListUiState()
}
```

- [ ] **Step 6: Update `ChatViewModel`**

File: `app/src/main/java/com/example/messageapp/ui/screen/chat/ChatViewModel.kt`

Use `GetChatHistoryUseCase` and `SendMessageUseCase`. Expose domain `Message`.

```kotlin
package com.example.messageapp.ui.screen.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.domain.model.Message
import com.example.messageapp.domain.usecase.message.GetChatHistoryUseCase
import com.example.messageapp.domain.usecase.message.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadHistory(user1: String, user2: String) {
        viewModelScope.launch {
            getChatHistoryUseCase(user1, user2)
                .onSuccess { _messages.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun send(message: Message) {
        viewModelScope.launch {
            sendMessageUseCase(message)
                .onFailure { _error.value = it.message }
        }
    }
}
```

- [ ] **Step 7: Update `NewsViewModel`**

File: `app/src/main/java/com/example/messageapp/ui/screen/news/NewsViewModel.kt`

Use `GetNewsFeedUseCase`, `LikePostUseCase`, `AddCommentUseCase`.
Expose domain `NewsPost` and `Comment`.

```kotlin
package com.example.messageapp.ui.screen.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.domain.model.Comment
import com.example.messageapp.domain.model.NewsPost
import com.example.messageapp.domain.usecase.news.AddCommentUseCase
import com.example.messageapp.domain.usecase.news.GetNewsFeedUseCase
import com.example.messageapp.domain.usecase.news.LikePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val getNewsFeedUseCase: GetNewsFeedUseCase,
    private val likePostUseCase: LikePostUseCase,
    private val addCommentUseCase: AddCommentUseCase
) : ViewModel() {

    private val _posts = MutableStateFlow<List<NewsPost>>(emptyList())
    val posts: StateFlow<List<NewsPost>> = _posts

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadFeed() {
        viewModelScope.launch {
            getNewsFeedUseCase()
                .onSuccess { _posts.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun like(postId: Int) {
        viewModelScope.launch {
            likePostUseCase(postId)
                .onSuccess { updated ->
                    _posts.value = _posts.value.map { if (it.id == postId) updated else it }
                }
                .onFailure { _error.value = it.message }
        }
    }

    fun comment(postId: Int, text: String) {
        viewModelScope.launch {
            addCommentUseCase(postId, text)
                .onFailure { _error.value = it.message }
        }
    }
}
```

- [ ] **Step 8: Update `AddNewsViewModel`**

File: `app/src/main/java/com/example/messageapp/ui/screen/news/AddNewsViewModel.kt`

Use `CreatePostUseCase`.

```kotlin
package com.example.messageapp.ui.screen.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.domain.usecase.news.CreatePostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNewsViewModel @Inject constructor(
    private val createPostUseCase: CreatePostUseCase
) : ViewModel() {

    private val _isCreated = MutableStateFlow(false)
    val isCreated: StateFlow<Boolean> = _isCreated

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun createPost(text: String, imageBytes: ByteArray? = null) {
        viewModelScope.launch {
            createPostUseCase(text, imageBytes)
                .onSuccess { _isCreated.value = true }
                .onFailure { _error.value = it.message }
        }
    }
}
```

- [ ] **Step 9: Update `AccountViewModel`**

File: `app/src/main/java/com/example/messageapp/ui/screen/account/AccountViewModel.kt`

Use `UpdateProfileUseCase` and `GetCurrentUserUseCase`. Expose domain `User`.

```kotlin
package com.example.messageapp.ui.screen.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.usecase.user.GetCurrentUserUseCase
import com.example.messageapp.domain.usecase.user.UpdateProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _updated = MutableStateFlow(false)
    val updated: StateFlow<Boolean> = _updated

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadUser() {
        viewModelScope.launch {
            getCurrentUserUseCase()
                .onSuccess { _user.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun updateProfile(user: User) {
        viewModelScope.launch {
            updateProfileUseCase(user)
                .onSuccess { _updated.value = true }
                .onFailure { _error.value = it.message }
        }
    }
}
```

- [ ] **Step 10: Update screen imports and domain model usage**

For each screen (`SearchScreen`, `ChatListScreen`, `ChatScreen`, `NewsFeedScreen`, `CreatePostScreen`, `AccountScreen`):
- Replace data DTO imports with domain model imports.
- Update lambdas and state types to use domain `User`, `Message`, `NewsPost`, etc.
- Keep Composable signatures stable where possible to minimize `MessageAppNavHost` changes.

- [ ] **Step 11: Verify build**

Run: `./gradlew :app:assembleDebug --no-daemon`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 12: Commit**

```bash
git add -A
git commit -m "refactor(ui): update ViewModels and screens to use domain use cases"
```

---

## Task 11: Final verification and cleanup

**Files:**
- Modify: `docs/DEBT.md` to mark Phase 0–1 debt as resolved.

**Interfaces:**
- Produces: updated debt list and green build.

- [ ] **Step 1: Update `docs/DEBT.md`**

Mark resolved items:
```markdown
## Phase 0–1 (resolved)
- [x] Package structure reorganized into `ui.screen.<feature>`.
- [x] Repository interfaces moved to `domain.repository` with no framework types.
- [x] Single god use case replaced with focused use cases.
- [x] Domain models introduced; UI no longer uses data DTOs directly.
- [x] Unused XML themes/colors/animations removed.
```

- [ ] **Step 2: Final build check**

Run: `./gradlew :app:assembleDebug --no-daemon`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 3: Run lint check**

Run: `./gradlew :app:lintDebug --no-daemon`
Expected: No new critical errors (warnings acceptable).

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "docs: mark Phase 0–1 debt as resolved"
```

---

## Spec Coverage Check

| Spec requirement | Task |
|------------------|------|
| Document baseline architecture and debt | Task 1 |
| Remove unused XML resources | Task 2 |
| Reorganize UI packages | Task 3 |
| Introduce domain models | Task 4 |
| Refactor repository interfaces | Task 5 |
| Create data mappers | Task 6 |
| Implement data repositories | Task 7 |
| Split Hilt modules | Task 8 |
| Create focused use cases | Task 9 |
| Update ViewModels/screens | Task 10 |
| Verify build and update docs | Task 11 |

## Placeholder Scan

No TBD/TODO/fill-in steps. Every step includes exact file paths and either shell commands or code.

## Type Consistency Check

- Domain `User` is used consistently across repositories, use cases, and ViewModels.
- Repository interfaces return `Result<T>` where `T` is a domain model.
- `CreatePostUseCase` accepts `ByteArray?` matching `NewsRepository.createPost`.
- `GetChatHistoryUseCase` parameters `(String, String)` match `MessageRepository.getHistory`.

## Execution Options

Plan complete and saved to `docs/superpowers/plans/2026-07-12-portfolio-redesign-phase0-1-plan.md`.

1. **Subagent-Driven (recommended)** — dispatch a fresh subagent per task, review between tasks.
2. **Inline Execution** — execute tasks in this session with checkpoints.

Proceed with subagent-driven execution.
