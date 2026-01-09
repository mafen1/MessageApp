package com.example.messageapp.ui.newsListScreen

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messageapp.R
import com.example.messageapp.core.logD
import com.example.messageapp.core.snackBar
import com.example.messageapp.databinding.FragmentNewsListBinding
import com.example.messageapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewsListFragment : BaseFragment<FragmentNewsListBinding>(FragmentNewsListBinding::inflate) {

    private val userArgs: NewsListFragmentArgs by navArgs()
    private val viewModel by viewModels<NewsViewModel>()

    override fun initView() {
        initBottomNavigationView()
        binding.fabCreatePost.setOnClickListener {
            val action = NewsListFragmentDirections.actionNewsListFragmentToAddNewsFragment(userArgs.user)
            findNavController().navigate(action)
        }

        viewModel.allNews()
        viewModel.saveUser(userArgs.user)
        initRecyclerView()
    }


    private fun initRecyclerView() {
        lifecycleScope.launch {
            viewModel.newsList.collect { news ->
                binding.rcNews.layoutManager = LinearLayoutManager(requireContext())
                binding.rcNews.adapter = NewsAdapter(news)
            }
        }
    }

    private fun initBottomNavigationView() {
        binding.bottomNavigationView.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.navSearch -> {
                    val action =
                        NewsListFragmentDirections.actionNewsListFragmentToNavSearch(userArgs.user)
                    findNavController().navigate(action)
                    true
                }
                R.id.navChat -> {
                    val action = NewsListFragmentDirections.actionNewsListFragmentToNavChat(userArgs.user)
                    findNavController().navigate(action)
                    true
                }

                R.id.navNews -> {
                    snackBar(binding.root, "Вы уже на данном экране")
                    true
                }

                else -> {
//                    logD("Ошибка")
                    true
                }
            }

        }
    }


}