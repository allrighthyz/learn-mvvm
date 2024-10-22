package com.transsion.mediaplayerdemo.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.transsion.mediaplayerdemo.databinding.SimpleListItemBinding

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    private val messages = mutableListOf<String>()

    class MessageViewHolder(val binding: SimpleListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: String) {
            binding.text1.text = message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = SimpleListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    fun updateMessages(newMessages: List<String>) {
        Log.d("Adapter", "Updating messages $messages")
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }
}

