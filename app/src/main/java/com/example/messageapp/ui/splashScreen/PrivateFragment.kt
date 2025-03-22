package com.example.messageapp.ui.splashScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.messageapp.R
import com.example.messageapp.databinding.FragmentPrivatyBinding

class PrivateFragment : Fragment() {

    lateinit var binding: FragmentPrivatyBinding

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
            findNavController().navigate(R.id.action_privateFragment_to_registerFragment)
        }
    }

}