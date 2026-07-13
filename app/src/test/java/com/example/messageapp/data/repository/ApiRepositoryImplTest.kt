package com.example.messageapp.data.repository

import com.example.messageapp.data.local.db.dao.MessageDao
import com.example.messageapp.data.network.api.service.ApiService
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.security.EncryptionManager
import com.google.gson.Gson
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class ApiRepositoryImplTest {

    @Mock
    private lateinit var apiService: ApiService

    @Mock
    private lateinit var messageDao: MessageDao

    @Mock
    private lateinit var encryptionManager: EncryptionManager

    private val gson = Gson()
    private lateinit var repository: ApiRepositoryImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = ApiRepositoryImpl(apiService, gson, messageDao, encryptionManager)
    }

    @Test
    fun `getAllUsers returns success list of domain users when api call succeeds`() = runTest {
        val expected = listOf(
            UserResponse(username = "@alice", name = "Alice"),
            UserResponse(username = "@bob", name = "Bob")
        )
        whenever(apiService.allUser()).thenReturn(expected)

        val result = repository.getAllUsers()

        assertTrue(result.isSuccess)
        assertEquals(
            listOf(
                User(name = "Alice", userName = "@alice"),
                User(name = "Bob", userName = "@bob")
            ),
            result.getOrNull()
        )
    }

    @Test
    fun `getAllUsers returns failure when api throws exception`() = runTest {
        val error = RuntimeException("Network error")
        whenever(apiService.allUser()).thenThrow(error)

        val result = repository.getAllUsers()

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }
}
