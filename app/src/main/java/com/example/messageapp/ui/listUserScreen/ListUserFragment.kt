package com.example.messageapp.ui.listUserScreen


import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messageapp.R
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.databinding.FragmentListUserBinding


class ListUserFragment : Fragment() {

    private lateinit var binding: FragmentListUserBinding
    private val viewModel by viewModels<ListUserViewModel>()
    private val userFragmentArgs: ListUserFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
        val user = userFragmentArgs.User
        binding.textView4.text = user?.userName
                                    // пользователь приложения
        viewModel.connectWebSocket(user.userName)
        initRecyclerView()
        initObserver()



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
                    Log.d("TAG", e.message.toString())
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun initBottomNavigation() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            run {
                when (item.itemId) {
                    R.id.navSearch ->
                        Toast.makeText(
                            requireContext(),
                            "Вы уже находитесь на данном экране",
                            Toast.LENGTH_LONG
                        ).show()
                    R.id.navChat ->
                        findNavController().navigate(R.id.action_listUserFragment_to_chatListFragment)

                    else -> {}
                }
            }
            return@setOnItemSelectedListener true
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun initNotification(message: String) {
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
    private fun initRecyclerView(){

        viewModel.foundUser.observe(viewLifecycleOwner){ user ->
            binding.recyclerView2.adapter = ListUserAdapter(user){ currentUser ->
                val message = "Заявка в друзья для пользователя ${currentUser.username}"
                viewModel.webSocketClient?.sendMessage("${userFragmentArgs.User.userName}:${currentUser.username}:$message")
                Log.d("TAG", "${userFragmentArgs.User.userName}:${currentUser.username}:$message")
            }
            binding.recyclerView2.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initObserver(){
        viewModel.messageNotification.observe(viewLifecycleOwner) { message ->
            initNotification(message)
        }
    }

}