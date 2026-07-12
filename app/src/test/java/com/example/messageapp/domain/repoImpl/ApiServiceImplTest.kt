package com.example.messageapp.domain.repoImpl

import com.example.messageapp.data.network.api.service.ApiService
import com.example.messageapp.data.network.model.UserResponse
import com.google.gson.Gson
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class ApiServiceImplTest {

    @Mock
    private lateinit var apiService: ApiService

    private val gson = Gson()
    private lateinit var repository: ApiServiceImpl

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        repository = ApiServiceImpl(apiService, gson)
    }

    @Test
    fun `allUser returns success list when api call succeeds`() = runTest {
        val expected = listOf(
            UserResponse(username = "@alice", name = "Alice"),
            UserResponse(username = "@bob", name = "Bob")
        )
        whenever(apiService.allUser()).thenReturn(expected)

        val result = repository.allUser()

        assertTrue(result.isSuccess)
        assertEquals(expected, result.getOrNull())
    }

    @Test
    fun `allUser returns failure when api throws exception`() = runTest {
        val error = RuntimeException("Network error")
        whenever(apiService.allUser()).thenThrow(error)

        val result = repository.allUser()

        assertTrue(result.isFailure)
        assertEquals(error, result.exceptionOrNull())
    }
}
