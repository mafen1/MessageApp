package com.example.messageapp.domain.usecase

import com.example.messageapp.domain.model.NewsPost
import com.example.messageapp.domain.repository.NewsRepository

class CreatePostUseCase constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(post: NewsPost, imageBytes: ByteArray? = null): Result<Unit> {
        return if (imageBytes != null) {
            newsRepository.createPostWithImage(post, imageBytes)
        } else {
            newsRepository.createPost(post)
        }
    }
}
