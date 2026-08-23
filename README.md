# MESSAGEAPP

Android-приложение для обмена сообщениями, разработанное на **Kotlin**.

## 📱 Описание

MESSAGEAPP — это клиентское Android-приложение для отправки и получения сообщений. Проект реализует базовый функционал мессенджера с интуитивным интерфейсом.

## 🚀 Технологии

- **Kotlin** — основной язык
- **Jetpack Compose** — декларативный UI + type-safe Navigation
- **Material Design 3** — компоненты и темизация
- **Hilt** — внедрение зависимостей
- **MVVM / Clean Architecture** — слои data/domain/ui
- **Room** — offline-first кэш сообщений и очередь отправки (outbox)
- **WorkManager** — синхронизация неотправленных сообщений при возврате сети
- **WebSocket** (java-websocket) — real-time чат с авто-reconnect и backoff
- **Retrofit / OkHttp** — REST API
- **DataStore** — хранение настроек и сессии
- **Coroutines / Flow / StateFlow** — асинхронность
- **E2E-шифрование**: AES-GCM + RSA (Android Keystore), ключи чатов с версионированием эпох
- **Coil** — загрузка изображений
- **LeakCanary, MockWebServer, JUnit + Mockito** — отладка и тестирование
- **Gradle (Kotlin DSL)** — система сборки
- **Android Studio / IntelliJ IDEA** — среда разработки

## 📋 Функциональность

- Отправка текстовых сообщений и изображений
- Сквозное (E2E) шифрование чатов: AES-GCM для сообщений, RSA-обёртка ключей с версионированием эпох на сервере — [архитектурная документация](docs/ARCHITECTURE.md)
- Список чатов, друзья и заявки в друзья
- Лента новостей с лайками и комментариями
- Оффлайн-режим: локальный кэш (Room) и очередь неотправленных сообщений (WorkManager)
- Статусы сообщений (отправлено / доставлено), дедупликация по clientMessageId
- Автоматический реконнект WebSocket с экспоненциальной задержкой

## 🛠 Установка и запуск

### Требования

- Android Studio Hedgehog или новее
- JDK 11+
- Android SDK 24+ (Android 7.0)

Запуск сервера — см. репозиторий [ServerMessage](https://github.com/mafen1/ServerMessage) (`docker compose up --build`). По умолчанию debug-сборка обращается к `http://10.0.2.2:8081` (эмулятор → localhost).

### Запуск проекта

1. Клонируйте репозиторий:

```bash
git clone https://github.com/mafen1/MESSAGEAPP.git
```

2. Откройте проект в Android Studio

3. Синхронизируйте Gradle-зависимости

4. Запустите на эмуляторе или физическом устройстве

## 📁 Структура проекта

```
MESSAGEAPP/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/messageapp/
│   │   │   ├── ui/mainScreen/MainActivity.kt   # Единственная Activity
│   │   │   ├── ui/navigation/                  # Навигация Compose
│   │   │   ├── ui/theme/                       # Тема, цвета, типографика
│   │   │   ├── ui/components/                  # Переиспользуемые компоненты
│   │   │   ├── ui/screen/                      # Экраны на Compose
│   │   │   ├── data/                           # Модели данных и сеть
│   │   │   └── domain/                         # Use cases и репозитории
│   │   └── res/                                # Ресурсы (drawable, strings)
│   └── build.gradle.kts                        # Зависимости модуля app
├── build.gradle.kts                            # Корневая конфигурация
├── settings.gradle.kts
└── gradle.properties
```

## 🔗 Связанные проекты

- **ServerMessage** — backend-сервер мессенджера (Ktor, PostgreSQL, Docker): [github.com/mafen1/ServerMessage](https://github.com/mafen1/ServerMessage)

## 📈 Возможности для расширения

- [x] Поддержка отправки изображений и файлов
- [ ] Уведомления о новых сообщениях
- [x] Светлая и тёмная тема в тёмно-синих тонах
- [x] Кэширование сообщений локально (Room)
- [x] Статусы сообщений (отправлено, доставлено) — осталось «прочитано»
- [x] Unit-тесты (ViewModel, репозитории, E2E-шифрование)
- [ ] UI-тесты

## 📸 Скриншоты

<table>
  <tr>
    <td><img src="screenshots/1.png" width="260"/></td>
    <td><img src="screenshots/2.png" width="260"/></td>
    <td><img src="screenshots/3.png" width="260"/></td>
  </tr>
  <tr>
    <td><img src="screenshots/4.png" width="260"/></td>
    <td><img src="screenshots/5.png" width="260"/></td>
    <td><img src="screenshots/6.png" width="260"/></td>
  </tr>
  <tr>
    <td><img src="screenshots/7.png" width="260"/></td>
    <td><img src="screenshots/8.png" width="260"/></td>
    <td><img src="screenshots/9.png" width="260"/></td>
  </tr>
  <tr>
    <td><img src="screenshots/10.png" width="260"/></td>
    <td><img src="screenshots/11.png" width="260"/></td>
    <td><img src="screenshots/12.png" width="260"/></td>
  </tr>
  <tr>
    <td><img src="screenshots/13.png" width="260"/></td>
    <td><img src="screenshots/14.png" width="260"/></td>
    <td><img src="screenshots/15.png" width="260"/></td>
  </tr>

 <tr>
    <td><img src="screenshots/16.png" width="260"/></td>
    <td><img src="screenshots/17.png" width="260"/></td>
 </tr>
</table>

## 👨‍💻 Автор

**mafen1** — [GitHub](https://github.com/mafen1)

## 📄 Лицензия

MIT
