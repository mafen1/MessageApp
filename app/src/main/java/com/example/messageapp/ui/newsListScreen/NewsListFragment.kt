package com.example.messageapp.ui.newsListScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.messageapp.R
import com.example.messageapp.core.logD
import com.example.messageapp.core.snackBar
import com.example.messageapp.databinding.FragmentNewsListBinding
import dagger.hilt.android.AndroidEntryPoint

// todo перемещение с newsListFragment на другие экраны
@AndroidEntryPoint
class NewsListFragment : Fragment() {

    lateinit var binding: FragmentNewsListBinding
    private val userArgs: NewsListFragmentArgs by navArgs()
    private val viewModel by viewModels<NewsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentNewsListBinding.inflate(layoutInflater, container, false)
        initView()
        initRecyclerView()
        return binding.root

    }


    private fun initView() {
        initBottomNavigationView()
        binding.button3.setOnClickListener {
            findNavController().navigate(R.id.action_newsListFragment_to_addNewsFragment)
        }
        viewModel.allNews()
    }


    private fun initRecyclerView() {
       viewModel.newsList.observe(viewLifecycleOwner) { news ->
           // todo переименовать recycler View + apply
           binding.recyclerView3.layoutManager = LinearLayoutManager(requireContext())
           binding.recyclerView3.adapter = NewsAdapter(news)
       }
    }


    private fun initBottomNavigationView() {
        binding.bottomNavigationView3.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.navSearch -> {
                    val action =
                        NewsListFragmentDirections.actionNewsListFragmentToNavSearch(userArgs.user)
                    findNavController().navigate(action)
                    true
                }
                // todo переделать
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