package com.example.messageapp.ui.privateScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.messageapp.R
import com.example.messageapp.databinding.FragmentPrivatyBinding
import com.example.messageapp.store.SharedPreference

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
            if (SharedPreference(requireContext()).getValueString("tokenJWT") != "") {
                viewModel.findUser(requireContext())

                viewModel.userResponse.observe(viewLifecycleOwner) { user ->
                    val action =
                        PrivateFragmentDirections.actionPrivateFragmentToListUserFragment(viewModel.userResponse.value!!)
                    findNavController().navigate(action)

                }
            } else {
                findNavController().navigate(R.id.action_privateFragment_to_registerFragment)
            }
        }
    }

}