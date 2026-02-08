package com.example.messageapp.ui.chatScreen

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
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

    override fun initView() {
        viewModel.findUser()
        initRecyclerView()

        lifecycleScope.launch {
            viewModel.connect(viewModel.findUserName())
        }


        initObservers()

        val user = userFragmentArgs.UserResponse


        binding.sendButton.setOnClickListener {
            sendMessage(user)
        }

        binding.imageView8.setOnClickListener {
            val action =
                ChatFragmentDirections.actionChatFragmentToChatListFragment(viewModel.user.value!!)
            findNavController().navigate(action)
        }

        binding.textView6.text = user.name

    }

    private fun initObservers() {
        observeMessageList()
        updateRecyclerViewMessageList()
    }

    private fun observeMessageList() {
        lifecycleScope.launch {
            viewModel.messageText.collect { message ->
                viewModel.updateMessageList(Message(message, false))
            }
        }
    }

    private fun updateRecyclerViewMessageList() {
        lifecycleScope.launch {
            viewModel.messageList.collect { list ->
                if (list != null) {
                    adapter?.updateList(list) // Обновляем адаптер, передавая новый список
                }
            }
        }
    }


    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ChatAdapter(mutableListOf())
        binding.recyclerView.adapter = adapter
    }

    private fun sendMessage(user: UserResponse) {
        try {
            // получаем username
            val targetUsername = user.username
            val messageContent = binding.editTextText.text.toString()
            // сообщение
            val messageToSend = "to:$targetUsername:$messageContent"

            viewModel.updateMessageList(Message(binding.editTextText.text.toString(), true))
            viewModel.webSocketClient?.sendMessage(messageToSend)


        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "Соединение не установлено ${e.toString()}",
                Toast.LENGTH_SHORT
            )
                .show()
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

