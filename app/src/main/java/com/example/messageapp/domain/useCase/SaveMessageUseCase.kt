package com.example.messageapp.domain.usecase

import com.example.messageapp.domain.model.Message
import com.example.messageapp.domain.repository.MessageRepository
import javax.inject.Inject

class SaveMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(message: Message, chatId: String) {
        messageRepository.saveMessage(message, chatId)
    }
}
