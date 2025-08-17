package com.example.messageapp.ui.registerScreen

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messageapp.core.snackBar
import com.example.messageapp.data.network.model.LoginRequest
import com.example.messageapp.data.network.model.User
import com.example.messageapp.databinding.FragmentRegisterBinding
import com.example.messageapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random


@AndroidEntryPoint
class RegisterFragment @Inject constructor() : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    private val viewModel by viewModels<RegisterViewModel>()

    override fun initView() {
        binding.btnSave.setOnClickListener {
            registrationAccount()
        }
        binding.tvLogin.setOnClickListener {
            registrationAccount()
            changeTextView()
        }

        initObserver()
    }

    private fun registrationAccount(){
        with(binding){
            if (edName.text.isNullOrEmpty() || edUserName.text.isNullOrEmpty() || binding.edPassword.text.isNullOrEmpty()){
                snackBar(binding.root, "Заполните все поля")
                return
            }
            if (binding.edUserName.text.first() != '@'){
                snackBar(binding.root, "Первый символ в UserName должен быть @ ")
                return
            }

            when(binding.tvRegistration.text){
                "Вход" -> handleLogin()
                else -> handleRegistration()
            }
        }
    }

    private fun handleRegistration() {
        val user = User(
            id = Random.nextInt(),
            name = binding.edName.text.toString(),
            userName = binding.edUserName.text.toString(),
            friend = listOf(),
            token = "",
            password = binding.edPassword.text.toString()
        )
        // Добавляем в бд

        viewModel.addAccount(
            user
        )

        val action =
            RegisterFragmentDirections.actionRegisterFragmentToListUserFragment(user)
        findNavController().navigate(action)
    }

    private fun handleLogin() {
        val loginRequest = LoginRequest(
            name = binding.edName.text.toString(),
            userName = binding.edUserName.text.toString(),
            password = binding.edPassword.text.toString(),

            )
        viewModel.loginAccount(loginRequest)
    }

    private fun initObserver() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            val action =
                RegisterFragmentDirections.actionRegisterFragmentToListUserFragment(user)
            findNavController().navigate(action)
        }
    }

    private fun changeTextView() {
        if (binding.tvRegistration.text == "Регистрация") {
            binding.tvRegistration.text = "Вход"
            binding.tvLogin.text = "Нету аккаунта? Создайте его"
        } else {
            binding.tvRegistration.text = "Регистрация"
            binding.tvLogin.text = "Есть уже аккаунт? Войдите"
        }
    }
}