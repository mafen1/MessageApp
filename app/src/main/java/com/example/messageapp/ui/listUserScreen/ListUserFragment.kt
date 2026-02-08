package com.example.messageapp.ui.listUserScreen


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
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


    override fun initView() {
        initSearchView()
        initBottomNavigation()

        viewModel.saveUserName(userArgs.User.userName)
        viewModel.connectWebSocket(userArgs.User.userName)

        logD("${ userArgs.User.userName } fjdkhsgafkjdhlg23432")

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
                } catch (_: Exception) {
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

        requireView().setupBottomNavigation(
            bottomNavigationView = binding.bottomNavigationView,
            currentDestinationId = R.id.navSearch
        ){
            navigateWithDirections(R.id.navChat) {
                ListUserFragmentDirections.actionListUserFragmentToChatListFragment(userArgs.User)
            }

            navigateWithDirections(R.id.navNews){
                ListUserFragmentDirections.actionNavSearchToNewsListFragment(userArgs.User)
            }

            navigateById(R.id.navAccount, R.id.action_newsListFragment_to_accountFragment)

        }

    }


    @SuppressLint("MissingPermission")
    private fun initNotification(){
        initNotificationChanel()

        val titleNotification = getString(R.string.titleNotification)
        val notification = NotificationCompat.Builder(requireContext(), ConstVariables.channelFriendNotification)
            .setSmallIcon(R.drawable.notification_24)
            .setContentTitle(titleNotification)
            .setContentText("")
            .setPriority(NotificationManager.IMPORTANCE_DEFAULT)
            .build()

        with(NotificationManagerCompat.from(requireContext())){
            notify(0,notification)
        }

    }

    private fun initNotificationChanel(){
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
        lifecycleScope.launch {
            viewModel.foundUser.collect { user ->
                logD(user.toString())
                binding.recyclerView2.adapter = ListUserAdapter(user) { currentUser ->
                    val message = "Заявка в друзья для пользователя ${currentUser.username}"

                    viewModel.sendMessage("${userArgs.User.userName}:${currentUser.username}:$message", currentUser.username)
                }
                binding.recyclerView2.layoutManager = LinearLayoutManager(requireContext())
            }
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            viewModel.messageNotification.collect { _ ->
                initNotification()
            }
        }
    }



    private fun hiddenRecyclerView() {
        binding.searchView.setOnCloseListener {
            binding.recyclerView2.adapter = ListUserAdapter(mutableListOf()) {}
            return@setOnCloseListener true
        }
    }


}