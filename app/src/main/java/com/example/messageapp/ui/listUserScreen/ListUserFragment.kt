package com.example.messageapp.ui.listUserScreen


import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messageapp.R
import com.example.messageapp.core.snackBar
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.databinding.FragmentListUserBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListUserFragment : Fragment() {

    private lateinit var binding: FragmentListUserBinding
    private val viewModel by viewModels<ListUserViewModel>()
    private val userArgs: ListUserFragmentArgs by navArgs()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListUserBinding.inflate(layoutInflater, container, false)
        initView()

        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initView() {
        initSearchView()
        initBottomNavigation()

        // todo отправка на сервер запроса на получение определенного юзера
        val user = userArgs.User
        binding.textView4.text = user.userName

        viewModel.saveUserName(user.userName)
        viewModel.connectWebSocket(user.userName)

        initRecyclerView()
        initObserver()
        hiddenRecyclerView()

    }

    private fun initSearchView() {
        binding.searchView.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // todo добавить проверку, что юзер ввел валидные данные
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
                }else{
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
                    val action = ListUserFragmentDirections.actionListUserFragmentToChatListFragment(userArgs.User)
                    findNavController().navigate(action)
                    true
                }

                R.id.navNews -> {
                    val action = ListUserFragmentDirections.actionNavSearchToNewsListFragment(userArgs.User)
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

        viewModel.foundUser.observe(viewLifecycleOwner) { user ->
            binding.recyclerView2.adapter = ListUserAdapter(user) { currentUser ->
                val message = "Заявка в друзья для пользователя ${currentUser.username}"
                viewModel.sendMessage("${userArgs.User.userName}:${currentUser.username}:$message")
            }
            binding.recyclerView2.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initObserver() {
        viewModel.messageNotification.observe(viewLifecycleOwner) { _ ->
            initNotification()
        }
    }

    private fun hiddenRecyclerView(){
        binding.searchView.setOnCloseListener {
            binding.recyclerView2.adapter = ListUserAdapter(mutableListOf()){}
            return@setOnCloseListener true
        }
    }


}