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
import com.example.messageapp.data.network.model.MessageRC
import com.example.messageapp.databinding.FragmentChatBinding


class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding
    private val viewModel by viewModels<ChatViewModel>()
    private val userFragmentArgs: ChatFragmentArgs by navArgs()
    private lateinit var adapter: ChatAdapter

    // todo перенести в view model
//    val messageList = mutableListOf<MessageRC>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChatBinding.inflate(layoutInflater, container, false)
        initView()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initView() {
        initObserver()
        viewModel.messageList.value?.let { initRecyclerView(it) }
        // todo отправку сообщений вынести в отдельную функцию
        val user = userFragmentArgs.UserResponse
        viewModel.connect(viewModel.findUserName(requireContext()))
        binding.imageView3.setOnClickListener {
            try {
                // Получаем username отправителя (текущий пользователь)
                val senderUsername = viewModel.findUserName(requireContext())
                val targetUsername = user.username
                // Имя получателя
                val messageContent = binding.editTextText.text.toString()
                // Формируем сообщение в формате "to:username:message"
                val messageToSend = "to:$targetUsername:$messageContent"

                viewModel.updateMessageList(MessageRC(binding.editTextText.text.toString(), true))
                viewModel.webSocketClient?.sendMessage(messageToSend)


            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Соединение не установлено", Toast.LENGTH_SHORT)
                    .show()
            }

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

            viewModel.updateMessageList(MessageRC(message, false))
//            initRecyclerView()
        }


        viewModel.messageList.observe(viewLifecycleOwner) { list ->
            Log.d("TAG", "Получено сообщений: ${list.size}")
            adapter.updateList(list) // Обновляем адаптер, передавая новый список
//            initRecyclerView(list)
        }
    }


    private fun initRecyclerView(messageList: MutableList<MessageRC>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ChatAdapter(messageList)
        binding.recyclerView.adapter = adapter
    }
}

