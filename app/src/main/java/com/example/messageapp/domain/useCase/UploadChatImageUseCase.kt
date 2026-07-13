package com.example.messageapp.domain.usecase

import com.example.messageapp.domain.repository.MessageRepository

class UploadChatImageUseCase constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(imageBytes: ByteArray): Result<String> {
        return messageRepository.uploadImage(imageBytes)
    }
}
