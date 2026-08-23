package com.example.messageapp.domain.usecase

import com.example.messageapp.core.ConstVariables
import com.example.messageapp.core.TokenStorage
import com.example.messageapp.domain.repository.ChatSocketRepository
import com.example.messageapp.domain.repository.MessageRepository
import com.example.messageapp.domain.security.EncryptionManager
import javax.inject.Inject

/**
 * Полный logout: закрывает сокет, стирает локальную переписку и чат-ключи,
 * сбрасывает сессию. Порядок важен: сначала сеть, потом локальные данные.
 */
class LogoutUseCase @Inject constructor(
    private val appPreferences: AppPreferencesUseCase,
    private val chatSocketRepository: ChatSocketRepository,
    private val messageRepository: MessageRepository,
    private val encryptionManager: EncryptionManager
) {
    suspend operator fun invoke() {
        chatSocketRepository.disconnect()
        messageRepository.clearAllLocalData()
        encryptionManager.clearAllChatKeys()

        appPreferences.setString(ConstVariables.tokenJWT, "")
        appPreferences.setString(ConstVariables.userName, "")
        appPreferences.setString(ConstVariables.nameUser, "")
        TokenStorage.clear()
    }
}
