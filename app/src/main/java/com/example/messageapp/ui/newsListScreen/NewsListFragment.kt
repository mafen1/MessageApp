package com.example.messageapp.ui.newsListScreen

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messageapp.R
import com.example.messageapp.databinding.FragmentNewsListBinding
import com.example.messageapp.setupBottomNavigation
import com.example.messageapp.ui.BaseFragment
import com.example.messageapp.ui.listUserScreen.ListUserFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

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

        requireView().setupBottomNavigation(
            bottomNavigationView = binding.bottomNavigationView,
            currentDestinationId = R.id.navNews
        ){
            navigateWithDirections(R.id.navSearch) {
                NewsListFragmentDirections.actionNewsListFragmentToNavSearch(userArgs.user)
            }

            navigateWithDirections(R.id.navChat){
                NewsListFragmentDirections.actionNewsListFragmentToNavChat(userArgs.user)
            }

            navigateById(R.id.navAccount, R.id.action_newsListFragment_to_accountFragment)

        }
    }


}