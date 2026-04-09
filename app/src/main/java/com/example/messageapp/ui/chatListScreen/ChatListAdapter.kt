package com.example.messageapp.ui.chatListScreen

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.databinding.ListChatHolderBinding
import javax.inject.Inject

class ChatListAdapter @Inject constructor(
    private val listUser: List<UserResponse>,
    private val onItemClick: (UserResponse) -> Unit,
    ) :
    RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder>() {

    private companion object {
        const val TAG = "ChatListAdapter"
    }

    private var items: MutableList<UserResponse> = listUser.toMutableList()

    fun updateData(newItems: List<UserResponse>) {
        Log.d(TAG, "updateData called with ${newItems.size} items: $newItems")
        items = newItems.toMutableList()
        Log.d(TAG, "items updated, calling notifyDataSetChanged")
        notifyDataSetChanged()
    }

    inner class ChatListViewHolder(private val binding: ListChatHolderBinding) :
        ViewHolder(binding.root) {
        fun bind(user: UserResponse) {
            Log.d(TAG, "bind: username=${user.username}, name=${user.name}")
            binding.username.text = user.username
            binding.tvUserId.text = user.name

            binding.root.setOnClickListener {
                Log.d(TAG, "clicked on user: ${user.username}")
                onItemClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        Log.d(TAG, "onCreateViewHolder")
        return ChatListViewHolder(
            ListChatHolderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "getItemCount: ${items.size}")
        return items.size
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: position=$position, username=${items[position].username}")
        holder.bind(items[position])
    }
}