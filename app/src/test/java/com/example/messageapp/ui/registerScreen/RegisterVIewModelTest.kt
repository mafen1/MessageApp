package com.example.messageapp.ui.registerScreen

import com.example.messageapp.data.network.model.LoginResponse
import com.example.messageapp.data.network.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterVIewModelTest {

    @Test
    fun loginResponseContainsCreatedUser() {
        val testUser = User(
            id = 1,
            name = "Анна Петрова",
            userName = "@anna_p_89",
            friend = listOf(),
            token = "",
            password = "secret123"
        )

        val response = LoginResponse(
            token = "token",
            expiresAt = "2026-01-01T00:00:00",
            user = testUser
        )

        assertEquals("@anna_p_89", response.user.userName)
        assertTrue(response.token.isNotBlank())
    }
}
