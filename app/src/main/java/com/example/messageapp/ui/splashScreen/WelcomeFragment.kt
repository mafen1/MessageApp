package com.example.messageapp.ui.splashScreen

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.messageapp.R
import com.example.messageapp.core.ConstVariables
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
            viewModel.loginUser()
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