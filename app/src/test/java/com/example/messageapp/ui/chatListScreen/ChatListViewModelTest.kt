package com.example.messageapp.ui.chatListScreen

import com.example.messageapp.core.UiState
import com.example.messageapp.data.network.model.FriendResponse
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.domain.useCase.ApiServiceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ChatListViewModelTest {

    @Mock
    private lateinit var apiUseCase: ApiServiceUseCase

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ChatListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        viewModel = ChatListViewModel(apiUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadChatList emits success with filtered friends`() = runTest {
        whenever(apiUseCase.getFriends("@me")).thenReturn(
            Result.success(FriendResponse(message = "ok", friends = listOf("@alice")))
        )
        whenever(apiUseCase.allUser()).thenReturn(
            Result.success(
                listOf(
                    UserResponse(username = "@alice", name = "Alice"),
                    UserResponse(username = "@bob", name = "Bob")
                )
            )
        )

        viewModel.loadChatList("@me", "Me")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        val users = (state as UiState.Success).data.users
        assertEquals(1, users.size)
        assertEquals("@alice", users[0].username)
    }

    @Test
    fun `loadChatList emits error when api fails`() = runTest {
        whenever(apiUseCase.getFriends("@me")).thenReturn(
            Result.failure(RuntimeException("Network error"))
        )

        viewModel.loadChatList("@me", "Me")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is UiState.Error)
    }
}
