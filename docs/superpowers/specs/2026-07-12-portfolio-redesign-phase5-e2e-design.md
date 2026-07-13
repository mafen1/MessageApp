# MessageApp — Phase 5: Basic E2E encryption

## Goal

Add end-to-end encryption for text messages so the server cannot read message content. Use a simplified approach suitable for a portfolio project.

## Approach

Per-chat symmetric AES-GCM key:
- When two users start a chat, the sender generates a random 256-bit AES key.
- The key is encrypted with the recipient's RSA public key and sent to the server.
- The recipient decrypts the chat key with their RSA private key stored in Android Keystore.
- All subsequent text messages in that chat are encrypted with the chat key using AES-GCM.
- Keys are stored in `EncryptedSharedPreferences` (AndroidX Security).

## Server changes

- `POST /keys/{username}` — upload user's RSA public key.
- `GET /keys/{username}` — retrieve user's RSA public key.
- `POST /chat-keys/{chatId}` — upload encrypted chat key for recipient.
- `GET /chat-keys/{chatId}` — retrieve encrypted chat key for current user.

Message payload becomes base64-encoded ciphertext; the server only relays bytes.

## Android changes

- `domain.security.EncryptionManager` — pure interface for encrypt/decrypt.
- `data.security.EncryptionManagerImpl` — AES-GCM + RSA + Keystore handling.
- `di/SecurityModule.kt` — binds `EncryptionManager`.
- Update `ChatWebSocketManager` to encrypt outgoing text and decrypt incoming.
- Add unit tests for encryption roundtrip.

## Success criteria

1. `./gradlew :app:assembleDebug` passes.
2. `./gradlew :app:testDebugUnitTest` passes.
3. Unit test: encrypt + decrypt returns original text.
4. Server exposes key endpoints and stores/returns keys.
