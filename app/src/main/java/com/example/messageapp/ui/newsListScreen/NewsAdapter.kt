package com.example.messageapp.ui.newsListScreen

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.messageapp.R
import com.example.messageapp.data.network.model.NewsRequest
import com.example.messageapp.data.network.model.NewsResponse
import com.example.messageapp.databinding.NewsHolderBinding
import javax.inject.Inject

class NewsAdapter @Inject constructor(private val newsList: MutableList<NewsResponse>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(private val binding: NewsHolderBinding) : ViewHolder(binding.root) {
        fun bind(news: NewsResponse) {
            binding.tvUserName.text = news.userName
            binding.tvDescription.text = news.text


            Glide.with(binding.root.context)
                .load("http://10.0.2.2:8081/images/${news.image}")
                .placeholder(R.drawable.news) // заглушка
                .error(R.drawable.chat)            // ошибка
                .into(binding.ivPhoto)
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