# MessageApp Architecture

## Overview

Android messenger client built with Kotlin and Jetpack Compose.
Single `ComponentActivity` (`MainActivity`) hosts the entire UI via Compose Navigation.

## Layers

```
ui              # Compose screens, ViewModels, Navigation, Theme
domain          # Pure Kotlin models, repository interfaces, use cases
data            # Remote API (Retrofit/OkHttp), local preferences, repository impls
```

### Dependency rule

- `domain` has no Android or framework dependencies.
- `data` depends on `domain` (implements repository interfaces).
- `ui` depends on `domain` and `di`.

## Modules

- `app`: application manifest, `MainActivity`, Hilt entry point.
- `data.network.api.service.ApiService`: Retrofit interface.
- `data.repository.ApiRepositoryImpl`: single repository implementation implementing all feature repository interfaces.
- `data.mapper.ModelMapper`: DTO ↔ domain mappers.
- `data.local.preferences.PreferencesDataStore`: DataStore-backed preferences.
- `domain.repository.*`: repository contracts.
- `domain.usecase.*`: single-responsibility use cases.
- `di.*`: focused Hilt modules (`NetworkModule`, `DataStoreModule`, `RepositoryModule`, `UseCaseModule`).

## Server

Ktor server lives in `/Users/mafen/ww/programming/kotlin/ServerMessage`.
It provides REST endpoints for auth/users/messages/news/friends and WebSocket delivery.
