package com.example.messageapp.ui.listUserScreen


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messageapp.R
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.core.logD
import com.example.messageapp.core.snackBar
import com.example.messageapp.data.network.model.AcceptFriendRequest
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.databinding.FragmentListUserBinding
import com.example.messageapp.setupBottomNavigation
import com.example.messageapp.ui.BaseFragment
import com.example.messageapp.ui.newsListScreen.NewsListFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListUserFragment : BaseFragment<FragmentListUserBinding>(FragmentListUserBinding::inflate) {

    private val viewModel by viewModels<ListUserViewModel>()
    private val userArgs: ListUserFragmentArgs by navArgs()


    private var userAdapter: ListUserAdapter? = null

    override fun initView() {
        initSearchView()
        initBottomNavigation()

        viewModel.saveUserName(userArgs.User.userName)
        viewModel.startPolling(userArgs.User.userName)

        logD("${userArgs.User.userName} fjdkhsgafkjdhlg23432")

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
                    viewModel.searchUser(userName, userArgs.User.userName)
                } catch (_: Exception) {
                    Toast.makeText(requireContext(), "Данного пользователя нет", Toast.LENGTH_LONG)
                        .show()
                }
                return true
            }


            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    viewModel.findUserByStr(newText, userArgs.User.userName)
                } else {
                    hiddenRecyclerView()
                }
                return true
            }

        })
    }

    private fun initBottomNavigation() {

        requireView().setupBottomNavigation(
            bottomNavigationView = binding.bottomNavigationView,
            currentDestinationId = R.id.navSearch
        ) {
            navigateWithDirections(R.id.navChat) {
                ListUserFragmentDirections.actionListUserFragmentToChatListFragment(userArgs.User)
            }

            navigateWithDirections(R.id.navNews) {
                ListUserFragmentDirections.actionNavSearchToNewsListFragment(userArgs.User)
            }

            navigateById(R.id.navAccount, R.id.action_newsListFragment_to_accountFragment)

        }

    }


    @SuppressLint("MissingPermission")
    private fun initNotification() {
        initNotificationChanel()

        val titleNotification = getString(R.string.titleNotification)
        val notification =
            NotificationCompat.Builder(requireContext(), ConstVariables.channelFriendNotification)
                .setSmallIcon(R.drawable.notification_24)
                .setContentTitle(titleNotification)
                .setContentText("")
                .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
                .build()

        with(NotificationManagerCompat.from(requireContext())) {
            notify(0, notification)
        }

    }

    private fun initNotificationChanel() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val chanel = NotificationChannel(
                ConstVariables.channelFriendNotification,
                ConstVariables.channelNameNotification,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                enableVibration(true)
            }
            NotificationManagerCompat.from(requireContext()).createNotificationChannel(chanel)
        }
    }

    private fun initRecyclerView() {
        userAdapter = ListUserAdapter(mutableListOf(), emptySet()) { currentUser ->
            viewModel.sendFriendRequest(userArgs.User.userName, currentUser.username)
        }
        binding.recyclerView2.adapter = userAdapter
        binding.recyclerView2.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            viewModel.foundUser.collect { users ->
                logD("foundUser: $users")
                userAdapter?.updateData(users, viewModel.pendingRequests.value)
            }
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel.messageNotification.collect { message ->
                logD("messageNotification: $message")
                if (message.isNotBlank()) {
                    initNotification()
                    showAcceptFriendDialog(message)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.pendingRequests.collect { pending ->
                userAdapter?.updateData(viewModel.foundUser.value, pending)
            }
        }

        lifecycleScope.launch {
            viewModel.friendRequestResult.collect { result ->
                result?.let {
                    snackBar(binding.root, it)
                    viewModel.resetFriendRequestResult()
                }
            }
        }
    }

    private fun showAcceptFriendDialog(message: String) {
        logD("showAcceptFriendDialog: message=$message")
        
        if (userArgs.User.userName.isBlank()) {
            logD("showAcceptFriendDialog: current user name is blank")
            return
        }
        
        if (message.isBlank()) {
            logD("showAcceptFriendDialog: message is blank, returning")
            return
        }

        // Server sends: "Заявка от @username"
        val senderUsername = if (message.startsWith("Заявка от ")) {
            message.removePrefix("Заявка от ").trim()
        } else if (message.contains(":")) {
            message.split(":").firstOrNull()?.trim()
        } else {
            message.trim()
        }
        
        logD("showAcceptFriendDialog: extracted senderUsername=$senderUsername")
        if (senderUsername.isNullOrEmpty() || senderUsername.isBlank()) {
            logD("showAcceptFriendDialog: blank username, returning")
            return
        }

        logD("showAcceptFriendDialog: showing dialog for $senderUsername")
        activity?.runOnUiThread {
            AlertDialog.Builder(requireContext())
                .setTitle("Заявка в друзья")
                .setMessage("Пользователь $senderUsername хочет добавить вас в друзья")
                .setPositiveButton("Принять") { _, _ ->
                    viewModel.acceptFriend(senderUsername, userArgs.User.userName)
                }
                .setNegativeButton("Отклонить") { _, _ ->
                    viewModel.rejectFriend(senderUsername, userArgs.User.userName)
                }
                .setCancelable(true)
                .show()
        }
    }


    private fun hiddenRecyclerView() {
        binding.searchView.setOnCloseListener {
            userAdapter?.updateData(mutableListOf(), emptySet())
            return@setOnCloseListener true
        }
    }
}