package com.example.messageapp.ui.chatScreen

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messageapp.R
import com.example.messageapp.core.logD
import com.example.messageapp.data.network.model.Message
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.databinding.FragmentChatBinding
import com.example.messageapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : BaseFragment<FragmentChatBinding>(FragmentChatBinding::inflate) {

    private val viewModel by viewModels<ChatViewModel>()
    private val userFragmentArgs: ChatFragmentArgs by navArgs()
    private var adapter: ChatAdapter? = null
    private var currentUserName: String = ""

    override fun initView() {
        viewModel.findUser()
        initRecyclerView()
        initToolbar()

        lifecycleScope.launch {
            currentUserName = viewModel.findUserName()
            val otherUser = userFragmentArgs.UserResponse
            viewModel.connect(currentUserName)
            viewModel.loadMessageHistory(currentUserName, otherUser.username)
        }

        initObservers()

        val user = userFragmentArgs.UserResponse

        binding.sendButton.setOnClickListener {
            sendMessage(user)
        }

        binding.toolbarName.text = user.name
    }

    private fun initToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            val action =
                ChatFragmentDirections.actionChatFragmentToChatListFragment(viewModel.user.value!!)
            findNavController().navigate(action)
        }
    }

    private fun initObservers() {
        updateRecyclerViewMessageList()
    }

    private fun updateRecyclerViewMessageList() {
        lifecycleScope.launch {
            viewModel.messageList.collect { list ->
                if (list != null) {
                    adapter?.updateList(list)
                    // Прокрутка к последнему сообщению
                    binding.recyclerView.scrollToPosition(list.size - 1)
                }
            }
        }
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        adapter = ChatAdapter(mutableListOf())
        binding.recyclerView.adapter = adapter
    }

    private fun sendMessage(user: UserResponse) {
        try {
            val targetUsername = user.username
            val messageContent = binding.editTextText.text.toString()
            if (messageContent.isBlank()) return

            val messageToSend = "to:$targetUsername:$messageContent"

            viewModel.updateMessageList(Message(messageContent, true))
            viewModel.webSocketClient?.sendMessage(messageToSend)

            binding.editTextText.text?.clear()
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                getString(R.string.chat_connection_error),
                Toast.LENGTH_SHORT
            ).show()
            logD(e.toString())
        }
    }

    override fun onDestroy() {
        viewModel.disconnect()
        binding.recyclerView.adapter = null
        adapter = null
        super.onDestroy()
    }
}
