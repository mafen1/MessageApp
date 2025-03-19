package com.example.messageapp.ui.chatScreen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.messageapp.data.network.webSocket.client.WebSocketManager
import com.example.messageapp.databinding.FragmentChatBinding


class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding
    private val chatViewModel by viewModels<ChatViewModel>()

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

        WebSocketManager().connect()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initView() {
        binding.imageView3.setOnClickListener {
            chatViewModel.chat(binding.editTextText.text.toString())
            Log.d("TAG", binding.editTextText.text.toString())
        }
    }

    override fun onDestroy() {
        chatViewModel.disconnect()
        super.onDestroy()
    }


}