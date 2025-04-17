package com.example.messageapp.ui.privateScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messageapp.R
import com.example.messageapp.data.network.model.User
import com.example.messageapp.databinding.FragmentPrivatyBinding

class PrivateFragment : Fragment() {

    lateinit var binding: FragmentPrivatyBinding
    private val viewModel by viewModels<PrivateViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPrivatyBinding.inflate(layoutInflater, container, false)

        initView()
        return binding.root
    }

    private fun initView() {
        binding.button.setOnClickListener {
            viewModel.findUser(requireContext())

            viewModel.userResponse.observe(viewLifecycleOwner) { user ->
                validateUser(user)
            }
        }
    }

    // проверка есть ли пользователь в базе данных
    private fun validateUser(user: User?) {
        if (user?.userName?.isNotEmpty() == true) {
            val action = PrivateFragmentDirections.actionPrivateFragmentToListUserFragment(user)
            findNavController().navigate(action)
        } else {
            findNavController().navigate(R.id.action_privateFragment_to_registerFragment)
        }
    }

}