package com.example.messageapp.ui.listUserScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.databinding.ListUserHolderBinding
import javax.inject.Inject


class ListUserAdapter @Inject constructor(
    private var listUser: MutableList<UserResponse>,
    private var pendingRequests: Set<String> = emptySet(),
    private val onAddFriend: (UserResponse) -> Unit
) :
    RecyclerView.Adapter<ListUserAdapter.ListUserViewHolder>() {

    fun updateData(users: List<UserResponse>, pending: Set<String>) {
        listUser = users.toMutableList()
        pendingRequests = pending
        notifyDataSetChanged()
    }

    inner class ListUserViewHolder(val binding: ListUserHolderBinding) : ViewHolder(binding.root) {
        fun bind(user: UserResponse) {

            binding.tvUserName.text = user.username
            binding.tvName.text = user.name

            val isPending = pendingRequests.contains(user.username)
            binding.button2.isEnabled = !isPending
            binding.button2.text = if (isPending) {
                binding.root.context.getString(com.example.messageapp.R.string.btn_request_sent)
            } else {
                binding.root.context.getString(com.example.messageapp.R.string.btn_add_friend)
            }

            binding.button2.setOnClickListener {
                if (!isPending) {
                    onAddFriend(user)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListUserViewHolder {
        return ListUserViewHolder(
            ListUserHolderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = listUser.size

    override fun onBindViewHolder(holder: ListUserViewHolder, position: Int) {
        holder.bind(listUser[position])
    }
}