package com.example.messageapp.ui.privateScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messageapp.databinding.FragmentPrivatyBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
            viewModel.findUser()
        }
        initObserver()
    }


    private fun initObserver() {
        viewModel.userResponse.observe(viewLifecycleOwner) { user ->
            val action =
                PrivateFragmentDirections.actionPrivateFragmentToListUserFragment(user)
            findNavController().navigate(action)

        }
    }

}