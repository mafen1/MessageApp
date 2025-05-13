package com.example.messageapp.ui.registerScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messageapp.data.network.model.LoginRequest
import com.example.messageapp.data.network.model.User
import com.example.messageapp.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random


@AndroidEntryPoint
class RegisterFragment @Inject constructor() : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentRegisterBinding.inflate(layoutInflater, container, false)
        initView()
        initObserver()
        return binding.root
    }


    private fun initView() {
        binding.btnSave.setOnClickListener {
            registrationAccount()
        }
        binding.tvLogin.setOnClickListener {
            registrationAccount()
        }
    }


    // todo доделать editPassword
    // todo перенести все if else во viewModel
    private fun registrationAccount() {
        if (binding.edName.text.isNotEmpty() && binding.edUserName.text.isNotEmpty()) {
            if (binding.edUserName.text.first() == '@') {
                if (binding.tvLogin.text == "Login"){
                    val loginRequest = LoginRequest(
                        userName = binding.edUserName.text.toString(),
                        password = binding.edName.text.toString()
                    )
                    viewModel.loginAccount(loginRequest)
                }else{
                    // создаем нового юзера
                    val user = User(
                        id = Random.nextInt(),
                        name = binding.edName.text.toString(),
                        userName = binding.edUserName.text.toString(),
                        friend = listOf(),
                        token = ""
                    )
                    // Добавляем в бд
                    viewModel.addAccount(
                        user,
                        requireContext()
                    )

                    val action =
                        RegisterFragmentDirections.actionRegisterFragmentToListUserFragment(user)
                    findNavController().navigate(action)
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Первый символ в UserName должен быть @ ",
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_LONG).show()
        }
    }

    private fun initObserver(){
        viewModel.foundUser.observe(viewLifecycleOwner){ user ->
            val action =
                RegisterFragmentDirections.actionRegisterFragmentToListUserFragment(user)
            findNavController().navigate(action)
        }
    }
}