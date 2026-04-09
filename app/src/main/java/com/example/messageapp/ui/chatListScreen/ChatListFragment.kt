package com.example.messageapp.ui.chatListScreen

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messageapp.R
import com.example.messageapp.core.logD
import com.example.messageapp.databinding.FragmentChatListBinding
import com.example.messageapp.setupBottomNavigation
import com.example.messageapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatListFragment : BaseFragment<FragmentChatListBinding>(FragmentChatListBinding::inflate) {

    private val viewModel by viewModels<ChatListViewModel>()
    private val userArgs: ChatListFragmentArgs by navArgs()

    override fun initView() {
        viewModel.loadChatList(userArgs.User.userName, userArgs.User.name)
        initObserver()
        initNavigate()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadChatList(userArgs.User.userName, userArgs.User.name)
    }

    private fun initObserver() {
        val adapter = ChatListAdapter(emptyList()) { selectedUser ->
            logD("Clicked on user: ${selectedUser.username}")
            val action =
                ChatListFragmentDirections.actionChatListFragmentToChatFragment(
                    selectedUser
                )
            findNavController().navigate(action)
        }
        binding.recyclerView1.adapter = adapter
        binding.recyclerView1.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                logD("isLoading changed: $isLoading")
                if (isLoading) {
                    binding.progressBar.visibility = android.view.View.VISIBLE
                    binding.recyclerView1.visibility = android.view.View.GONE
                    binding.emptyState.visibility = android.view.View.GONE
                } else {
                    binding.progressBar.visibility = android.view.View.GONE
                }
            }
        }

        lifecycleScope.launch {
            viewModel.displayUsers.collect { users ->
                logD("displayUsers collected: ${users.size} items: $users")
                adapter.updateData(users)

                if (!viewModel.isLoading.value) {
                    if (users.isEmpty()) {
                        logD("No users to display, showing empty state")
                        binding.recyclerView1.visibility = android.view.View.GONE
                        binding.emptyState.visibility = android.view.View.VISIBLE
                    } else {
                        logD("Displaying ${users.size} users in RecyclerView")
                        binding.recyclerView1.visibility = android.view.View.VISIBLE
                        binding.emptyState.visibility = android.view.View.GONE
                        // Принудительно обновляем RecyclerView
                        binding.recyclerView1.invalidate()
                    }
                }
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