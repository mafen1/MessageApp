package com.example.messageapp.ui.registerScreen

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.messageapp.core.snackBar
import com.example.messageapp.data.network.model.LoginRequest
import com.example.messageapp.data.network.model.User
import com.example.messageapp.databinding.FragmentRegisterBinding
import com.example.messageapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class RegisterFragment @Inject constructor() : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

    private val viewModel: RegisterViewModel by viewModels()

    override fun initView() {
        binding.btnSave.setOnClickListener {
            registrationAccount()
        }

        binding.btnLogin.setOnClickListener {
            changeTextView()
        }
        initObservers()
    }

    private fun registrationAccount() {
        with(binding) {
            if (edName.text.isNullOrEmpty() || edUserName.text.isNullOrEmpty() || edPassword.text.isNullOrEmpty()) {
                snackBar(binding.root, "Заполните все поля")
                return
            }
            if (edUserName.text?.first() != '@') {
                snackBar(binding.root, "Первый символ в UserName должен быть @")
                return
            }

            when (tvRegistration.text) {
                "Вход" -> handleLogin()
                else -> handleRegistration()
            }
        }
    }

    // todo id
    private fun handleRegistration() {
        val user = User(
            id = Random.nextInt(),
            name = binding.edName.text.toString(),
            userName = binding.edUserName.text.toString(),
            friend = emptyList(),
            token = "",
            password = binding.edPassword.text.toString()
        )
        viewModel.addAccount(user)
    }

    private fun handleLogin() {
        val loginRequest = LoginRequest(
            name = binding.edName.text.toString(),
            userName = binding.edUserName.text.toString(),
            password = binding.edPassword.text.toString()
        )
        viewModel.loginAccount(loginRequest)
    }

    private fun initObservers() {
        observeErrors()
        observeRegistrationSuccess()
        observeUser()
    }

    private fun observeRegistrationSuccess(){
        lifecycleScope.launch {
            viewModel.registrationSuccess.collect { user ->
                user?.let {
                    val action =
                        RegisterFragmentDirections.actionRegisterFragmentToListUserFragment(it)
                    findNavController().navigate(action)
                    viewModel.resetRegistrationState()
                }
            }
        }
    }

    private fun observeUser(){
        lifecycleScope.launch {
            viewModel.currentUser.collect { user ->
                user?.let {
                    val action =
                        RegisterFragmentDirections.actionRegisterFragmentToListUserFragment(it)
                    findNavController().navigate(action)
                }
            }
        }
    }

    private fun observeErrors(){
        lifecycleScope.launch {
            viewModel.error.collect { errorMsg ->
                errorMsg?.let {
                    snackBar(binding.root, it)
                    viewModel.resetError()
                }
            }
        }
    }



    private fun changeTextView() {
        if (binding.tvRegistration.text == "Регистрация") {
            binding.tvRegistration.text = "Вход"
            binding.btnLogin.text = "Нет аккаунта? Создайте его"
        } else {
            binding.tvRegistration.text = "Регистрация"
            binding.btnLogin.text = "Уже есть аккаунт? Войдите"
        }
    }
}