package com.example.messageapp.core

/** Канонический id чата между двумя пользователями: отсортированные username'ы через "__". */
fun buildChatId(user1: String, user2: String): String =
    listOf(user1, user2).sorted().joinToString("__")
