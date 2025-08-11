package com.example.messageapp.ui.privateScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.messageapp.R
import com.example.messageapp.databinding.FragmentPrivatyBinding
import com.example.messageapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PrivateFragment : BaseFragment<FragmentPrivatyBinding>(FragmentPrivatyBinding::inflate) {

//    lateinit var binding: FragmentPrivatyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPrivatyBinding.inflate(layoutInflater, container, false)
        initView()
        return binding!!.root
    }


    override fun initView() {
        binding?.button?.setOnClickListener {
            findNavController().navigate(R.id.action_privateFragment_to_registerFragment)
        }
    }


}