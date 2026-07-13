package com.example.messageapp.ui.screen.chatlist

import com.example.messageapp.core.UiState
import com.example.messageapp.domain.model.User
import com.example.messageapp.domain.usecase.GetAllUsersUseCase
import com.example.messageapp.domain.usecase.GetFriendsUseCase
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
    private lateinit var getFriendsUseCase: GetFriendsUseCase

    @Mock
    private lateinit var getAllUsersUseCase: GetAllUsersUseCase

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ChatListViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        viewModel = ChatListViewModel(getFriendsUseCase, getAllUsersUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadChatList emits success with filtered friends`() = runTest {
        whenever(getFriendsUseCase.invoke("@me")).thenReturn(
            Result.success(listOf("@alice"))
        )
        whenever(getAllUsersUseCase.invoke()).thenReturn(
            Result.success(
                listOf(
                    User(name = "Alice", userName = "@alice"),
                    User(name = "Bob", userName = "@bob")
                )
            )
        )

        viewModel.loadChatList("@me", "Me")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is UiState.Success)
        val users = (state as UiState.Success).data.users
        assertEquals(1, users.size)
        assertEquals("@alice", users[0].userName)
    }

    @Test
    fun `loadChatList emits error when api fails`() = runTest {
        whenever(getFriendsUseCase.invoke("@me")).thenReturn(
            Result.failure(RuntimeException("Network error"))
        )

        viewModel.loadChatList("@me", "Me")
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is UiState.Error)
    }
}
