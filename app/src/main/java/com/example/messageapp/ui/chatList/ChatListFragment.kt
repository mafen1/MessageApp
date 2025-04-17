package com.example.messageapp.ui.chatList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messageapp.R
import com.example.messageapp.databinding.FragmentChatListBinding


class ChatListFragment : Fragment() {

    private lateinit var binding: FragmentChatListBinding

    private val viewModel by viewModels<ChatListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatListBinding.inflate(layoutInflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        initAdapter()
        initObserver()
        initNavigate()
    }

    private fun initAdapter() {
        viewModel.allUser()

    }

    private fun initObserver(){
        viewModel.listUser.observe(viewLifecycleOwner){ user ->

            val adapter = ChatListAdapter(user) { selectedUser ->
                // Откроваем новый фрагмент при клике
                val action = ChatListFragmentDirections.actionChatListFragmentToChatFragment(selectedUser)
                findNavController().navigate(action)
            }
            binding.recyclerView1.adapter = adapter
            binding.recyclerView1.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun initNavigate(){
        binding.bottomNavigationView2.setOnItemSelectedListener { item ->
            run {
                when (item.itemId) {
                    R.id.navSearch ->

                        findNavController().navigate(R.id.action_listUserFragment_to_chatListFragment)

                    R.id.navChat ->
                        Toast.makeText(requireContext(),
                            "Вы уже на данном экране",
                            Toast.LENGTH_LONG).show()

                    else -> {}
                }
            }
            return@setOnItemSelectedListener true
        }
    }


}