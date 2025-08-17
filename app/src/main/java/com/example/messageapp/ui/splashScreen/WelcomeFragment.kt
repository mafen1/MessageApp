package com.example.messageapp.ui.splashScreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.messageapp.R
import com.example.messageapp.databinding.FragmentWelcomeBinding
import com.example.messageapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WelcomeFragment : BaseFragment<FragmentWelcomeBinding>(FragmentWelcomeBinding::inflate) {

    private val viewModel by viewModels<WelcomeViewModel>()

    override fun initView() {
        lifecycleScope.launch {
            initObserver()
            viewModel.findUser()
            delay(3000)
        }

    }

    private fun initObserver() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                val action =
                    WelcomeFragmentDirections.actionWelcomeFragmentToNavSearch(user)
                findNavController().navigate(action)
            } else {
                findNavController().navigate(R.id.action_welcomeFragment_to_privateFragment)
            }
        }
    }
}