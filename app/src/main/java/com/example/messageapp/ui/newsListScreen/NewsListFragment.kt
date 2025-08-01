package com.example.messageapp.ui.newsListScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.messageapp.R
import com.example.messageapp.core.logD
import com.example.messageapp.core.snackBar
import com.example.messageapp.databinding.FragmentNewsListBinding
import dagger.hilt.android.AndroidEntryPoint

// todo перемещение с newsListFragment на другие экраны
@AndroidEntryPoint
class NewsListFragment : Fragment() {

    lateinit var binding: FragmentNewsListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNewsListBinding.inflate(layoutInflater, container, false)

        initView()

        return binding.root
    }


    private fun initView() {
        initBottomNavigationView()
        binding.button3.setOnClickListener {
            findNavController().navigate(R.id.action_newsListFragment_to_addNewsFragment)
        }

    }


    private fun initBottomNavigationView() {
        binding.bottomNavigationView3.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.navSearch -> {
                    findNavController().navigate(R.id.action_newsListFragment_to_navSearch)
                    true
                }

                R.id.navChat -> {
                    findNavController().navigate(R.id.action_newsListFragment_to_navChat)
                    true
                }

                R.id.navNews -> {
                    snackBar(binding.root, "Вы уже на данном экране")
                    true
                }

                else -> {
                    logD("Ошибка")
                    true
                }
            }

        }
    }


}