package com.example.messageapp.ui.newsListScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.messageapp.R
import com.example.messageapp.core.ConstVariables
import com.example.messageapp.data.network.model.NewsResponse
import com.example.messageapp.databinding.NewsHolderBinding
import javax.inject.Inject

class NewsAdapter @Inject constructor(private val newsList: MutableList<NewsResponse>) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(private val binding: NewsHolderBinding) : ViewHolder(binding.root) {
        fun bind(news: NewsResponse) {

            binding.tvUserName.text = news.userName
            binding.tvDescription.text = news.text

            if (news.image != null) {
                Glide.with(binding.root.context)
                    .load("${ConstVariables.url}/images/${news.image}.jpg")
                    .placeholder(R.drawable.news) // заглушка
                    .error(R.drawable.chat)            // ошибка
                    .into(binding.ivPhoto)
            }else{
                binding.ivPhoto.setImageResource(R.drawable.news)
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