package com.example.messageapp.ui.newsListScreen

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.messageapp.R
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.data.network.model.NewsRequest
import com.example.messageapp.data.network.model.NewsResponse
import com.example.messageapp.databinding.NewsHolderBinding
import javax.inject.Inject

class NewsAdapter @Inject constructor(private val newsList: MutableList<NewsResponse>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(private val binding: NewsHolderBinding) : ViewHolder(binding.root) {
        fun bind(news: NewsResponse) {
            binding.authorName.text = news.nameAuthor
            binding.authorUsername.text = news.userNameAuthor
            binding.postText.text = news.description
            binding.postTime.text = news.date
            binding.likeCount.text = news.countLike.toString()
            binding.commentCount.text = news.countComment.toString()

            if (news.newsImage?.isNotEmpty() == true) {

                Glide.with(binding.root.context)
                    .load("${ConstVariables.url}/images/${news.newsImage}.jpg")
                    .placeholder(R.drawable.news)
                    .error(R.drawable.chat)
                    .into(binding.postImage)

            }else{
                binding.postImage.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            NewsHolderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = newsList.size

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(newsList[position])
    }


}