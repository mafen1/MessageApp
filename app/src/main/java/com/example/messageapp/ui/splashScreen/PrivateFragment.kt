package com.example.messageapp.ui.splashScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.messageapp.R
import com.example.messageapp.databinding.FragmentPrivatyBinding

class PrivateFragment : Fragment() {

     lateinit var binding: FragmentPrivatyBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_privaty, container, false)
    }

    private fun initView(){
        binding.button.setOnClickListener {

        }
    }

}