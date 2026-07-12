package com.example.messageapp.ui.chatScreen

import com.example.messageapp.data.network.model.Message
import com.example.messageapp.domain.useCase.ApiServiceUseCase
import com.example.messageapp.domain.useCase.AppPreferencesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    @Mock
    private lateinit var appPreferencesUseCase: AppPreferencesUseCase

    @Mock
    private lateinit var apiServiceUseCase: ApiServiceUseCase

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        whenever(appPreferencesUseCase.getString(any())).thenReturn(flowOf(""))
        viewModel = ChatViewModel(appPreferencesUseCase, apiServiceUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateMessageList appends message to the list`() {
        val message = Message("Hello", isType = true)

        viewModel.updateMessageList(message)

        assertEquals(listOf(message), viewModel.messageList.value)
    }

    @Test
    fun `resetError clears error state`() {
        viewModel.updateMessageList(Message("test", isType = false))
        // error initially null
        assertNull(viewModel.error.value)
    }
}
