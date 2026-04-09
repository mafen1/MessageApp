package com.example.messageapp.ui.chatScreen

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.messageapp.data.network.model.Message
import com.example.messageapp.databinding.ChatHolderLeftBinding
import com.example.messageapp.databinding.ChatHolderRightBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ChatAdapter @Inject constructor(private var messageList: MutableList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        const val VIEW_TYPE_MINE = 1
        const val VIEW_TYPE_THEIRS = 0
        const val TAG = "ChatAdapter"
    }

    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    inner class ChatViewRightHolder(private val binding: ChatHolderRightBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindRight(message: Message) {
            binding.textView3.text = message.message
            binding.messageTime.text = timeFormat.format(Date())
            binding.checkIcon.visibility = View.VISIBLE
            Log.d(TAG, "bindRight: message=${message.message}, isType=${message.isType}")
        }
    }

    inner class ChatViewLeftHolder(private val binding: ChatHolderLeftBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindLeft(message: Message) {
            binding.textView3.text = message.message
            binding.messageTime.text = timeFormat.format(Date())
            Log.d(TAG, "bindLeft: message=${message.message}, isType=${message.isType}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d(TAG, "onCreateViewHolder: viewType=$viewType")
        return when (viewType) {
            VIEW_TYPE_MINE -> {
                val binding = ChatHolderRightBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ChatViewRightHolder(binding)
            }

            else -> {
                val binding = ChatHolderLeftBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ChatViewLeftHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        Log.d(TAG, "onBindViewHolder: position=$position, message=${message.message}, isType=${message.isType}, viewType=${getItemViewType(position)}")
        when (holder) {
            is ChatViewLeftHolder -> holder.bindLeft(message)
            is ChatViewRightHolder -> holder.bindRight(message)
        }
    }

    override fun getItemCount(): Int = messageList.size

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        Log.d(TAG, "getItemViewType: position=$position, isType=${message.isType}")
        return if (message.isType) {
            VIEW_TYPE_MINE
        } else {
            VIEW_TYPE_THEIRS
        }
    }

    fun updateList(newList: List<Message>) {
        Log.d(TAG, "updateList: size=${newList.size}")
        messageList.clear()
        messageList.addAll(newList)
        notifyDataSetChanged()
    }
}
