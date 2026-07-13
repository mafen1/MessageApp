package com.example.messageapp.ui.screen.chat

import com.example.messageapp.domain.model.Message
import com.example.messageapp.domain.model.SocketState
import com.example.messageapp.domain.repository.ChatSocketRepository
import com.example.messageapp.domain.usecase.AppPreferencesUseCase
import com.example.messageapp.domain.usecase.GetChatHistoryUseCase
import com.example.messageapp.domain.usecase.SaveMessageUseCase
import com.example.messageapp.domain.usecase.UploadChatImageUseCase
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
    private lateinit var getChatHistoryUseCase: GetChatHistoryUseCase

    @Mock
    private lateinit var uploadChatImageUseCase: UploadChatImageUseCase

    @Mock
    private lateinit var saveMessageUseCase: SaveMessageUseCase

    @Mock
    private lateinit var chatSocketRepository: ChatSocketRepository

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        MockitoAnnotations.openMocks(this)
        whenever(appPreferencesUseCase.getString(any())).thenReturn(flowOf(""))
        whenever(chatSocketRepository.connectionState).thenReturn(kotlinx.coroutines.flow.MutableStateFlow(SocketState.Disconnected))
        whenever(chatSocketRepository.observeMessages()).thenReturn(kotlinx.coroutines.flow.emptyFlow())
        viewModel = ChatViewModel(appPreferencesUseCase, getChatHistoryUseCase, uploadChatImageUseCase, saveMessageUseCase, chatSocketRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateMessageList appends message to the list`() {
        val message = Message(text = "Hello", isFromMe = true)

        viewModel.updateMessageList(message)

        assertEquals(listOf(message), viewModel.messageList.value)
    }

    @Test
    fun `resetError clears error state`() {
        viewModel.updateMessageList(Message(text = "test", isFromMe = false))
        assertNull(viewModel.error.value)
    }
}
