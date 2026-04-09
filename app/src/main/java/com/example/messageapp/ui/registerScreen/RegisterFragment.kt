package com.example.messageapp.ui.registerScreen

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.messageapp.R
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
class RegisterFragment @Inject constructor() :
    BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate) {

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
                snackBar(binding.root, getString(R.string.error_fill_all_fields))
                return
            }
            if (edUserName.text?.first() != '@') {
                snackBar(binding.root, getString(R.string.error_username_at))
                return
            }

            when (tvRegistration.text) {
                getString(R.string.LoginTV) -> handleLogin()
                else -> handleRegistration()
            }
        }
    }

    private fun handleRegistration() {
        with(binding) {
            val user = User(
                id = Random.nextInt(),
                name = edName.text.toString(),
                userName = edUserName.text.toString(),
                friend = emptyList(),
                token = "",
                password = edPassword.text.toString()
            )
            viewModel.addAccount(user)
        }
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

    private fun observeRegistrationSuccess() {
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

    private fun observeUser() {
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

    private fun observeErrors() {
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
        if (binding.tvRegistration.text == getString(R.string.RegistrationTV)) {
            binding.tvRegistration.text = getString(R.string.LoginTV)
            binding.btnSave.text = getString(R.string.btn_login)
            binding.btnLogin.text = getString(R.string.CreateAccountTv)
            binding.textInputName.visibility = android.view.View.GONE
        } else {
            binding.tvRegistration.text = getString(R.string.RegistrationTV)
            binding.btnSave.text = getString(R.string.btn_register)
            binding.btnLogin.text = getString(R.string.has_account)
            binding.textInputName.visibility = android.view.View.VISIBLE
        }
    }
}
