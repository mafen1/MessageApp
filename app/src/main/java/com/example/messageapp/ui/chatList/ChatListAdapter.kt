package com.example.messageapp.ui.chatList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.databinding.ListChatHolderBinding

class ChatListAdapter(
    private val listUser: MutableList<UserResponse>,
    private val onItemClick: (UserResponse) -> Unit
) :
    RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>() {

    inner class ChatListViewHolder(private val binding: ListChatHolderBinding) :
        ViewHolder(binding.root) {
        fun bind(user: UserResponse) {
            binding.tvUserName.text = user.username
            binding.tvName.text = user.name

            binding.root.setOnClickListener {
                onItemClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        return ChatListViewHolder(
            ListChatHolderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = listUser.size


    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        holder.bind(listUser[position])
    }
}