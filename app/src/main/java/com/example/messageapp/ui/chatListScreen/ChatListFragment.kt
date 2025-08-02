package com.example.messageapp.ui.chatListScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messageapp.R
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.databinding.FragmentChatListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatListFragment : Fragment() {

    private lateinit var binding: FragmentChatListBinding
    private val viewModel by viewModels<ChatListViewModel>()
    private val userArgs: ChatListFragmentArgs by navArgs()

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

    private fun initObserver() {

        viewModel.listUser.observe(viewLifecycleOwner) { user ->

            val adapter =
                ChatListAdapter(user, userArgs.User.userName) { selectedUser ->
                    // Откроваем новый фрагмент при клике
                    val action =
                        ChatListFragmentDirections.actionChatListFragmentToChatFragment(
                            selectedUser
                        )
                    findNavController().navigate(action)
                }
            binding.recyclerView1.adapter = adapter
            binding.recyclerView1.layoutManager = LinearLayoutManager(requireContext())

            val indexYourAccount = user.indexOf(
                UserResponse(
                    name = userArgs.User.name,
                    username = userArgs.User.userName
                )
            )
            user.removeAt(indexYourAccount)
            adapter.notifyItemRemoved(indexYourAccount)
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