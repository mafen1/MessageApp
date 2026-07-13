package com.example.messageapp.ui.screen.auth

import com.example.messageapp.domain.model.LoggedInUser
import com.example.messageapp.domain.model.User
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthViewModelTest {

    @Test
    fun loginResponseContainsCreatedUser() {
        val testUser = User(
            id = 1,
            name = "Анна Петрова",
            userName = "@anna_p_89",
            friends = emptyList(),
            token = ""
        )

        val response = LoggedInUser(
            token = "token",
            expiresAt = "2026-01-01T00:00:00",
            user = testUser
        )

        assertEquals("@anna_p_89", response.user.userName)
        assertTrue(response.token.isNotBlank())
    }
}
