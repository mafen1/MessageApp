package com.example.messageapp.ui.chatScreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messageapp.R
import com.example.messageapp.data.network.model.Message
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.databinding.FragmentChatBinding


class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding
    private val viewModel by viewModels<ChatViewModel>()
    private val userFragmentArgs: ChatFragmentArgs by navArgs()
    private lateinit var adapter: ChatAdapter




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChatBinding.inflate(layoutInflater, container, false)
        initView()
        return binding.root
    }




    private fun initView() {
        initObserver()
        viewModel.messageList.value?.let { initRecyclerView(it) }

        val user = userFragmentArgs.UserResponse
        // подключаемся к веб сокету
        viewModel.connect(viewModel.findUserName(requireContext()))
        Log.d("TAG", viewModel.findUserName(requireContext()))
        binding.imageView3.setOnClickListener {
            sendMessage(user)
        }

        binding.imageView8.setOnClickListener {
            findNavController().navigate(R.id.action_chatFragment_to_chatListFragment)
        }

        binding.textView6.text = user.name


    }

    override fun onDestroy() {
        viewModel.disconnect()
        Log.d("TAG", "destroy")
        super.onDestroy()
    }

    private fun initObserver() {

        viewModel.messageText.observe(viewLifecycleOwner) { message ->

            viewModel.updateMessageList(Message(message, false))
        }


        viewModel.messageList.observe(viewLifecycleOwner) { list ->
            Log.d("TAG", "Получено сообщений: ${list.size}")
            adapter.updateList(list) // Обновляем адаптер, передавая новый список
        }
    }


    private fun initRecyclerView(messageList: MutableList<Message>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ChatAdapter(messageList)
        binding.recyclerView.adapter = adapter
    }

    private fun sendMessage(user: UserResponse){
        try {
            // получаем юзер нейм
            val targetUsername = user.username
            val messageContent = binding.editTextText.text.toString()
            // сообщение
            val messageToSend = "to:$targetUsername:$messageContent"

            viewModel.updateMessageList(Message(binding.editTextText.text.toString(), true))
            viewModel.webSocketClient?.sendMessage(messageToSend)


        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Соединение не установлено", Toast.LENGTH_SHORT)
                .show()
        }
    }
}

