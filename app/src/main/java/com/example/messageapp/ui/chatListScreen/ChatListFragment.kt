package com.example.messageapp.ui.chatListScreen

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messageapp.R
import com.example.messageapp.databinding.FragmentChatListBinding
import com.example.messageapp.setupBottomNavigation
import com.example.messageapp.ui.BaseFragment
import com.example.messageapp.ui.listUserScreen.ListUserFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        lifecycleScope.launch {
            viewModel.filteredUsers(userArgs.User.userName, userArgs.User.name)
                viewModel.listUsers.collect { user ->

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
    }


    private fun initNavigate() {
        requireView().setupBottomNavigation(
            bottomNavigationView = binding.bottomNavigationView2,
            currentDestinationId = R.id.navChat
        ){
            navigateWithDirections(R.id.navNews) {
                ChatListFragmentDirections.actionNavChatToNewsListFragment(userArgs.User)
            }

            navigateWithDirections(R.id.navSearch){
                ChatListFragmentDirections.actionChatListFragmentToListUserFragment(userArgs.User)
            }

            navigateById(R.id.navAccount, R.id.action_newsListFragment_to_accountFragment)

        }
    }


}