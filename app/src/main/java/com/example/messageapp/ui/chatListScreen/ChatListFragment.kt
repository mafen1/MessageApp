package com.example.messageapp.ui.chatListScreen

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messageapp.R
import com.example.messageapp.databinding.FragmentChatListBinding
import com.example.messageapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatListFragment : BaseFragment<FragmentChatListBinding>(FragmentChatListBinding::inflate) {

    private val viewModel by viewModels<ChatListViewModel>()
    private val userArgs: ChatListFragmentArgs by navArgs()

    override fun initView() {
        initAdapter()
        initObserver()
        initNavigate()
    }

    private fun initAdapter() {
        viewModel.allUser()
    }

    private fun initObserver() {

        viewModel.filteredUsers(userArgs.User.userName, userArgs.User.name)
            .observe(viewLifecycleOwner) { user ->

                val adapter =
                    ChatListAdapter(user) { selectedUser ->
                        // Откроваем новый фрагмент при клике
                        val action =
                            ChatListFragmentDirections.actionChatListFragmentToChatFragment(
                                selectedUser
                            )
                        findNavController().navigate(action)
                    }
                binding.recyclerView1.adapter = adapter
                binding.recyclerView1.layoutManager = LinearLayoutManager(requireContext())

            }

    }


    private fun initNavigate() {
        binding.bottomNavigationView2.setupWithNavController(findNavController())
        binding.bottomNavigationView2.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.navSearch -> {

                    val action =
                        ChatListFragmentDirections.actionChatListFragmentToListUserFragment(
                            userArgs.User
                        )
                    findNavController().navigate(action)
                    true
                }

                R.id.navChat -> {
                    Toast.makeText(
                        requireContext(),
                        "Вы уже на данном экране",
                        Toast.LENGTH_LONG
                    ).show()
                    true
                }

                R.id.navNews -> {
                    val action =
                        ChatListFragmentDirections.actionNavChatToNewsListFragment(userArgs.User)
                    findNavController().navigate(action)
                }

                else -> {}
            }

            return@setOnItemSelectedListener true
        }
    }


}