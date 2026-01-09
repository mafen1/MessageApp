package com.example.messageapp.ui.listUserScreen


import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.app.NotificationCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messageapp.R
import com.example.messageapp.core.snackBar
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.databinding.FragmentListUserBinding
import com.example.messageapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListUserFragment : BaseFragment<FragmentListUserBinding>(FragmentListUserBinding::inflate) {

    private val viewModel by viewModels<ListUserViewModel>()
    private val userArgs: ListUserFragmentArgs by navArgs()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun initView() {
        initSearchView()
        initBottomNavigation()

        viewModel.saveUserName(userArgs.User.userName)
        viewModel.connectWebSocket(userArgs.User.userName)

        initRecyclerView()
        initObserver()
        hiddenRecyclerView()


    }

    private fun initSearchView() {
        binding.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                try {
                    val userName = UserRequest(
                        username = query!!
                    )
                    viewModel.searchUser(userName)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Данного пользователя нет", Toast.LENGTH_LONG)
                        .show()
                }
                return true
            }


            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    viewModel.findUserByStr(newText)
                } else {
                    hiddenRecyclerView()
                }
                return true
            }

        })
    }

    private fun initBottomNavigation() {
        binding.bottomNavigationView.setupWithNavController(findNavController())
        binding.bottomNavigationView.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.navSearch -> {
                    snackBar(binding.root, "Вы уже находитесь на данном экране")
                    true
                }

                R.id.navChat -> {
                    val action =
                        ListUserFragmentDirections.actionListUserFragmentToChatListFragment(userArgs.User)
                    findNavController().navigate(action)
                    true
                }

                R.id.navNews -> {
                    val action =
                        ListUserFragmentDirections.actionNavSearchToNewsListFragment(userArgs.User)
                    findNavController().navigate(action)
                    true
                }

                else -> true
            }

            return@setOnItemSelectedListener true
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun initNotification() {
        val chanel = NotificationChannel(
            "friend_request",
            "khdsgf",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager =
            requireContext().getSystemService(NotificationManager::class.java)

        notificationManager.createNotificationChannel(chanel)

        val notification = NotificationCompat.Builder(requireContext(), "friend_request")
            .setContentTitle("Новая заявка в друзья")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
        notificationManager.notify(1, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initRecyclerView() {
        lifecycleScope.launch {
            viewModel.foundUser.collect { user ->
                binding.recyclerView2.adapter = ListUserAdapter(user) { currentUser ->
                    val message = "Заявка в друзья для пользователя ${currentUser.username}"
                    viewModel.sendMessage("${userArgs.User.userName}:${currentUser.username}:$message")
                }
                binding.recyclerView2.layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initObserver() {
        lifecycleScope.launch {
            viewModel.messageNotification.collect { _ ->
                initNotification()
            }
        }
    }

    private fun hiddenRecyclerView() {
        val searchView = binding.searchView as androidx.appcompat.widget.SearchView
        searchView.setOnCloseListener {
            binding.recyclerView2.adapter = ListUserAdapter(mutableListOf()) {}
            return@setOnCloseListener true
        }
    }


}