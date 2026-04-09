package com.example.messageapp.ui.accountScreen

import androidx.navigation.fragment.findNavController
import com.example.messageapp.R
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.databinding.FragmentAccountBinding
import com.example.messageapp.domain.useCase.AppPreferencesUseCase
import com.example.messageapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class AccountFragment : BaseFragment<FragmentAccountBinding>(FragmentAccountBinding::inflate) {

    @Inject
    lateinit var appPreferencesUseCase: AppPreferencesUseCase

    override fun initView() {
        loadUserData()
        setupLogout()
    }

    private fun loadUserData() {
        val name = runBlocking {
            appPreferencesUseCase.getString(ConstVariables.nameUser).first()
        }
        val username = runBlocking {
            appPreferencesUseCase.getString(ConstVariables.userName).first()
        }

        binding.tvName.text = name.ifBlank { getString(R.string.account_title) }
        binding.tvUsername.text = username.ifBlank { "" }
    }

    private fun setupLogout() {
        binding.btnLogout.setOnClickListener {
            runBlocking {
                appPreferencesUseCase.setString(ConstVariables.tokenJWT, "")
                appPreferencesUseCase.setString(ConstVariables.userName, "")
                appPreferencesUseCase.setString(ConstVariables.nameUser, "")
            }
            findNavController().navigate(R.id.privateFragment)
        }
    }
}
