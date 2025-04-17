package com.example.messageapp.ui.chatScreen

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.messageapp.data.network.model.MessageRC
import com.example.messageapp.databinding.ChatHolderLeftBinding
import com.example.messageapp.databinding.ChatHolderRightBinding

class ChatAdapter(private val messageList: MutableList<MessageRC>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        const val VIEW_TYPE_MINE = 1
        const val VIEW_TYPE_THEIRS = 0
    }


    inner class ChatViewRightHolder(private val binding: ChatHolderRightBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindRight(message: MessageRC) {
                binding.textView3.text = message.message

        }
    }

    inner class ChatViewLeftHolder(private val binding: ChatHolderLeftBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindLeft(message: MessageRC) {
            binding.textView3.text = message.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            // Для ваших сообщений (справа)
                VIEW_TYPE_MINE -> {
                    val binding = ChatHolderRightBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    Log.d("TAG", "Создан Right")
                    ChatViewRightHolder(binding)
                }
            else ->{
                val binding = ChatHolderLeftBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                Log.d("TAG", "Создан Left")
                ChatViewLeftHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        when(holder){
            is ChatViewLeftHolder -> holder.bindLeft(message)
            is ChatViewRightHolder -> holder.bindRight(message)
        }
    }

    override fun getItemCount(): Int = messageList.size


    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message.isType) {
            VIEW_TYPE_MINE // Свое сообщение (правый холдер)
        } else {
            VIEW_TYPE_THEIRS // Чужое сообщение (левый холдер)
        }
    }

    fun updateList(newList: List<MessageRC>) {
        messageList.clear()
        messageList.addAll(newList)
        notifyDataSetChanged() // Обновляем адаптер
    }
}