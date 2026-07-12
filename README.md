# MESSAGEAPP

Android-приложение для обмена сообщениями, разработанное на **Kotlin**.

## 📱 Описание

MESSAGEAPP — это клиентское Android-приложение для отправки и получения сообщений. Проект реализует базовый функционал мессенджера с интуитивным интерфейсом.

## 🚀 Технологии

- **Kotlin** — основной язык
- **Android SDK** — платформа
- **Jetpack Compose** — декларативный UI
- **Compose Navigation** — навигация между экранами
- **Material Design 3** — компоненты и темизация
- **Hilt** — внедрение зависимостей
- **Coil** — загрузка изображений
- **Gradle (Kotlin DSL)** — система сборки
- **Android Studio / IntelliJ IDEA** — среда разработки

## 📋 Функциональность

- Отправка текстовых сообщений
- Отображение списка сообщений
- Реализация клиент-серверного взаимодействия
- Современный Material Design интерфейс

## 🛠 Установка и запуск

### Требования

- Android Studio Arctic Fox или новее
- JDK 11+
- Android SDK 21+ (Android 5.0)

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

- **SERVERMESSAGE** — backend-сервер для обмена сообщениями: [github.com/mafen1/SERVERMESSAGE](https://github.com/mafen1/SERVERMESSAGE)

## 📈 Возможности для расширения

- [x] Поддержка отправки изображений и файлов
- [ ] Уведомления о новых сообщениях
- [x] Светлая и тёмная тема в тёмно-синих тонах
- [ ] Кэширование сообщений локально (Room)
- [ ] Статусы сообщений (отправлено, доставлено, прочитано)
- [ ] Unit и UI-тесты

## 📸 Скриншоты

> Добавьте скриншоты приложения в папку `screenshots/`

## 👨‍💻 Автор

**mafen1** — [GitHub](https://github.com/mafen1)

## 📄 Лицензия

MIT
