package com.example.messageapp.ui.accountScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.databinding.FragmentAccountBinding
import com.example.messageapp.domain.useCase.AppPreferencesUseCase
import com.example.messageapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint


// todo переделать
@AndroidEntryPoint
class AccountFragment() : BaseFragment<FragmentAccountBinding>(FragmentAccountBinding::inflate) {

    override fun initView() {
//        binding.textView4.text = appPreferencesUseCase.getString(ConstVariables.userName).toString()
    }


}