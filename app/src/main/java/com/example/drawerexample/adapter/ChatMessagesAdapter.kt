package com.example.drawerexample.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.drawerexample.Message
import com.example.drawerexample.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

enum class Message {
    SENT, RECEIVED
}

class ChatMessagesAdapter(private val parentFragment: Fragment) : RecyclerView.Adapter<ChatMessagesAdapter.ChatMessageViewHolder>() {

    lateinit var messages : ArrayList<Message>

    class ChatMessageViewHolder(val view : View, val parentFragment : Fragment, adapter : ChatMessagesAdapter) : RecyclerView.ViewHolder(view) {

        val textContent : TextView = view.findViewById(R.id.chat_item_content)
    }

    override fun getItemViewType(position: Int): Int {
        return when ( messages[position].sender == Firebase.auth.currentUser?.uid ) {
            true -> com.example.drawerexample.adapter.Message.SENT.ordinal
            false -> com.example.drawerexample.adapter.Message.RECEIVED.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val layoutToInflate = when (viewType) {
            com.example.drawerexample.adapter.Message.SENT.ordinal -> R.layout.chat_message_sent_item
            com.example.drawerexample.adapter.Message.RECEIVED.ordinal -> R.layout.chat_message_received_item
            else -> R.layout.chat_message_sent_item
        }
        val view = LayoutInflater
                .from(parent.context).inflate(layoutToInflate, parent, false)

        return ChatMessageViewHolder(view, parentFragment, this)
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        holder.textContent.text = messages[position].text
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}