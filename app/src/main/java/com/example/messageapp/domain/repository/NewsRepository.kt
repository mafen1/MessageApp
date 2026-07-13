package com.example.messageapp.domain.repository

import com.example.messageapp.domain.model.NewsPost

interface NewsRepository {
    suspend fun getNewsFeed(): Result<List<NewsPost>>
    suspend fun createPost(post: NewsPost): Result<Unit>
    suspend fun createPostWithImage(post: NewsPost, imageBytes: ByteArray): Result<Unit>
    suspend fun toggleLike(newsId: Int, userName: String): Result<NewsPost>
    suspend fun addComment(newsId: Int, userName: String, text: String): Result<NewsPost>
}
