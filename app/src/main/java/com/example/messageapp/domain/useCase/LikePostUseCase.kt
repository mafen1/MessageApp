package com.example.messageapp.domain.usecase

import com.example.messageapp.domain.model.NewsPost
import com.example.messageapp.domain.repository.NewsRepository

class LikePostUseCase constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(newsId: Int, userName: String): Result<NewsPost> {
        return newsRepository.toggleLike(newsId, userName)
    }
}
