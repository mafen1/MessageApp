package com.example.messageapp.ui.listUserScreen

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.messageapp.data.network.model.UserResponse
import com.example.messageapp.databinding.ListUserHolderBinding

class ListUserAdapter(
    val listUser: MutableList<UserResponse>,
    private val onItemClick: (UserResponse) -> Unit
) :
    RecyclerView.Adapter<ListUserAdapter.ListUserViewHolder>() {

    inner class ListUserViewHolder(val binding: ListUserHolderBinding) : ViewHolder(binding.root) {
        fun bind(user: UserResponse) {

            binding.tvUserName.text = user.username
            binding.tvName.text = user.name

            binding.button2.setOnClickListener {
                onItemClick(user)
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