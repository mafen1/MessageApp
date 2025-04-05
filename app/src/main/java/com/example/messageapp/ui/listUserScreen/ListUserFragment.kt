package com.example.messageapp.ui.listUserScreen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels

import androidx.navigation.fragment.navArgs
import com.example.messageapp.R
import com.example.messageapp.data.network.model.UserRequest
import com.example.messageapp.databinding.FragmentListUserBinding


class ListUserFragment : Fragment() {

    private lateinit var binding: FragmentListUserBinding
    private val viewModel by viewModels<ListUserViewModel>()

    private val userFragmentArgs: ListUserFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListUserBinding.inflate(layoutInflater, container,false)
        initView()

        return binding. root
    }

    private fun initView() {
        val user = userFragmentArgs.User
        binding.textView4.text = user?.userName

        initSearchView()
    }

    private fun initSearchView(){
        binding.searchView.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // todo добавить проверку, что юзер ввел валидные данные
                try {
                    val userName = UserRequest(
                        username = query!!
                    )
                    viewModel.searchUser(userName)
                }catch (e: Exception){
                    Log.d("TAG", e.message.toString())
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }




}