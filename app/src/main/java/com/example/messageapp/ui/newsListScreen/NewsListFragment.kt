package com.example.messageapp.ui.newsListScreen

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messageapp.R
import com.example.messageapp.core.logD
import com.example.messageapp.core.snackBar
import com.example.messageapp.databinding.FragmentNewsListBinding
import com.example.messageapp.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NewsListFragment : BaseFragment<FragmentNewsListBinding>(FragmentNewsListBinding::inflate) {

    private val userArgs: NewsListFragmentArgs by navArgs()
    private val viewModel by viewModels<NewsViewModel>()

    override fun initView() {
        initBottomNavigationView()
        binding.button3.setOnClickListener {
            findNavController().navigate(R.id.action_newsListFragment_to_addNewsFragment)
        }
        viewModel.allNews()

        initRecyclerView()
    }


    private fun initRecyclerView() {
       viewModel.newsList.observe(viewLifecycleOwner) { news ->
           binding.rcNews.layoutManager = LinearLayoutManager(requireContext())
           binding.rcNews.adapter = NewsAdapter(news)
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