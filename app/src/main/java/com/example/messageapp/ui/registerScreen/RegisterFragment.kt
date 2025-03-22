package com.example.messageapp.ui.registerScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messageapp.R
import com.example.messageapp.data.model.User
import com.example.messageapp.databinding.FragmentRegisterBinding
import kotlinx.serialization.json.Json


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



    private fun initView(){
        binding.button2.setOnClickListener{
            val id = 2

            val user = User(
                id,
                name = binding.editTextText2.text.toString()
            )
            val json = Json.encodeToString(user)
            // добавление аккаунта в базу данных
            viewModel.addAccount(
                json
            )
            findNavController().navigate(R.id.action_registerFragment_to_chatFragment)
        }
    }
}