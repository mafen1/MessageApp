package com.example.messageapp.ui.newsListScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.messageapp.R
import com.example.messageapp.databinding.FragmentNewsListBinding
import dagger.hilt.android.AndroidEntryPoint


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
//        val gallery =
//            registerForActivityResult(ActivityResultContracts.GetContent()) { galleryUri ->
//                try {
//                   binding.imageView10.setImageURI(galleryUri)
//                } catch (e: Exception) {
//                    logD(e.toString())
//                }
//            }
        binding.button3.setOnClickListener {
            findNavController().navigate(R.id.action_newsListFragment_to_addNewsFragment)
        }
    }


}