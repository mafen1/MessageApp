package com.example.messageapp.domain.security

/**
 * Серверная копия обёртки чат-ключа с эпохой публикации.
 * Эпоха монотонно растёт при каждой ротации и используется для диагностики;
 * согласование выполняется сравнением развёрнутых байт ключа.
 */
data class WrappedKeyCopy(
    val wrappedKey: String,
    val version: Long
)
