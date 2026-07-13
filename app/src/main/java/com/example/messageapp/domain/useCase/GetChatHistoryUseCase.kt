package com.example.messageapp.domain.usecase

import com.example.messageapp.domain.model.Message
import com.example.messageapp.domain.repository.MessageRepository

class GetChatHistoryUseCase constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(currentUser: String, otherUser: String): Result<List<Message>> {
        return messageRepository.getMessages(currentUser, otherUser)
    }
}
