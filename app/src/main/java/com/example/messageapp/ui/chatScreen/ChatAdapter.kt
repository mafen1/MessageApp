package com.example.messageapp.ui.chatScreen

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.messageapp.databinding.ChatHolderBinding

class ChatAdapter : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    val messageList = listOf("")

    inner class ChatViewHolder(private val binding: ChatHolderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {

            val layoutParams = binding.root.layoutParams as LinearLayout.LayoutParams
            layoutParams.gravity = Gravity.START // Устанавливаем gravity вправо
            binding.root.layoutParams = layoutParams
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            ChatHolderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind()
    }
}