package com.example.messageapp.ui.registerScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messageapp.data.network.model.User
import com.example.messageapp.databinding.FragmentRegisterBinding
import kotlin.random.Random


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentRegisterBinding.inflate(layoutInflater, container, false)

        initView()

        return binding.root
    }


    private fun initView() {
        binding.btnSave.setOnClickListener {
            addAccount()
        }
    }

    private fun addAccount() {
        if (binding.edName.text.isNotEmpty() && binding.edUserName.text.isNotEmpty()) {
            if (binding.edUserName.text.first() == '@') {
                // добавление аккаунта в базу данных

                val user = User(
                    id = Random.nextInt(),
                    name = binding.edName.text.toString(),
                    userName = binding.edUserName.text.toString(),
                    friend = listOf(),
                    token = ""
                )

                viewModel.addAccount(
                    user,
                    requireContext()
                )

                val action =
                    RegisterFragmentDirections.actionRegisterFragmentToListUserFragment(user)
                findNavController().navigate(action)
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

}