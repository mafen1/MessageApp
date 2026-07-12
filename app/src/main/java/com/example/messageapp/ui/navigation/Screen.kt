package com.example.messageapp.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object WelcomeRoute

@Serializable
object OnboardingRoute

@Serializable
object AuthRoute

@Serializable
data class SearchRoute(
    val userName: String,
    val name: String
)

@Serializable
data class ChatListRoute(
    val userName: String,
    val name: String
)

@Serializable
data class ChatRoute(
    val currentUserName: String,
    val currentName: String,
    val otherUserName: String,
    val otherName: String
)

@Serializable
data class NewsFeedRoute(
    val userName: String,
    val name: String
)

@Serializable
data class CreatePostRoute(
    val userName: String,
    val name: String
)

@Serializable
data class AccountRoute(
    val userName: String,
    val name: String
)
