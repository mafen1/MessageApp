package com.example.messageapp.ui.registerScreen

import com.example.messageapp.data.network.model.User
import com.example.messageapp.domain.useCase.ApiServiceUseCase
import com.example.messageapp.domain.useCase.AppPreferencesUseCase
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

class RegisterVIewModelTest {

    val apiServiceUseCaseTest = mock<ApiServiceUseCase>()
    val appPreferenceTest = mock<AppPreferencesUseCase>()
    private lateinit var mockWebServer: MockWebServer

    @Test
    fun addAccount() {
        val testUser = User(
            id = 1,
            name = "Анна Петрова",
            userName = "@anna_p_89",
            friend = listOf(),
            token = "",
            password = "secret123"
        )

        val mockResponse = MockResponse().setBody(
            """"token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJ1c2VyLXNlcnZlciIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3QvIiwiZXhwIjoxNzYwNjYwMjczfQ.EWbuQJiM9k7sjdulVlxsFF_IjCcC71CwUvoPV-cc7FA",
    "expiresAt": "2025-10-17T00:17:53.950859700",
    "user": {
        "id": 42,
        "name": "Анна Петрова",
        "userName": "anna_p_89",
        "friend": [],
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.xJ34a9mTqQ",
        "password": "secret123"
    }
}"""
        )
            .addHeader("Content-Type", "application/json")
        mockWebServer.enqueue(mockResponse)


    }

    @After
    fun afterEach() {
        Mockito.reset(apiServiceUseCaseTest)
        Mockito.reset(appPreferenceTest)
    }

    @Before
    fun setUp() {

    }
}