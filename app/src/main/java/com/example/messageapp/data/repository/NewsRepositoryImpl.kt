package com.example.messageapp.data.repository

import com.example.messageapp.data.mapper.toDomain
import com.example.messageapp.data.mapper.toDto
import com.example.messageapp.data.network.api.service.ApiService
import com.example.messageapp.data.network.model.CommentRequest
import com.example.messageapp.data.network.model.LikeRequest
import com.example.messageapp.domain.model.NewsPost
import com.example.messageapp.domain.repository.NewsRepository
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val gson: Gson
) : NewsRepository {

    override suspend fun getNewsFeed(): Result<List<NewsPost>> = safeApiCall {
        apiService.allNews().map { it.toDomain() }
    }

    override suspend fun createPost(post: NewsPost): Result<Unit> = safeApiCall {
        apiService.uploadNewsWithOutImage(post.toDto())
    }

    override suspend fun createPostWithImage(post: NewsPost, imageBytes: ByteArray): Result<Unit> = safeApiCall {
        val newsRequest = post.toDto()
        val requestBody = gson.toJson(newsRequest)
            .toRequestBody("application/json".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData(
            "file",
            "news_${System.currentTimeMillis()}.jpg",
            imageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull(), 0, imageBytes.size)
        )
        apiService.uploadNews(part, requestBody)
    }

    override suspend fun toggleLike(newsId: Int, userName: String): Result<NewsPost> = safeApiCall {
        apiService.toggleLike(LikeRequest(newsId, userName)).toDomain()
    }

    override suspend fun addComment(newsId: Int, userName: String, text: String): Result<NewsPost> = safeApiCall {
        apiService.addComment(CommentRequest(newsId, userName, text)).toDomain()
    }
}
