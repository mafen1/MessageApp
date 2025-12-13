package com.example.messageapp.ui.privateScreen

import androidx.navigation.fragment.findNavController
import com.example.messageapp.R
import com.example.messageapp.databinding.FragmentPrivatyBinding
import com.example.messageapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrivateFragment : BaseFragment<FragmentPrivatyBinding>(FragmentPrivatyBinding::inflate) {

    override fun initView() {
        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_privateFragment_to_registerFragment)
        }
    }

}