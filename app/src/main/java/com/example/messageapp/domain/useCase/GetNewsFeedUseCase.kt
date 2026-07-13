package com.example.messageapp.domain.usecase

import com.example.messageapp.domain.model.NewsPost
import com.example.messageapp.domain.repository.NewsRepository

class GetNewsFeedUseCase constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(): Result<List<NewsPost>> {
        return newsRepository.getNewsFeed()
    }
}
