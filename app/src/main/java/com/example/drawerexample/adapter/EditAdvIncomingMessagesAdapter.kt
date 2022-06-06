package com.example.drawerexample.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.drawerexample.R
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.fragment.findNavController

class EditAdvIncomingMessagesAdapter(private val parentFragment : Fragment, private val advertisementID : String, private val userID : String) : RecyclerView.Adapter<EditAdvIncomingMessagesAdapter.IncomingAdvMessageViewHolder>() {
    var incomingMessages = ArrayList<Bundle>()
    var incomingRequests = ArrayList<Bundle>()

    var allItems : HashMap<String, String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomingAdvMessageViewHolder {
        val view = parentFragment.layoutInflater.inflate(R.layout.edit_adv_chat_notification_card, parent, false)
        return IncomingAdvMessageViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (allItems == null)
            return 0
        return allItems!!.size
    }

    override fun onBindViewHolder(holder: IncomingAdvMessageViewHolder, position: Int) {
        val element = allItems?.toList()?.get(position)

        if (element != null) {
            val requestingUser = element.second
            val otherUserID = element.first
            val text = parentFragment.resources.getString(R.string.edit_adv_chat_card_text)
                .format(requestingUser)
            val args = Bundle().apply {
                putString("advertisementID", advertisementID)
                putString("userID", userID)
                putString("otherUserID", otherUserID) // TODO: Change this
                putBoolean("userIsAdvertiser", true)
            }
            holder.incomingMessageText.text = text
            holder.goToChatButton.setOnClickListener {
                parentFragment.findNavController()
                    .navigate(R.id.action_nav_show_adv_to_nav_chat, args)
            }
        }
    }

    class IncomingAdvMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val incomingMessageText : TextView = itemView.findViewById(R.id.edit_adv_incoming_message_text)
        val goToChatButton : ImageButton = itemView.findViewById(R.id.edit_adv_go_to_chat_button)
    }

    fun addRequest(request : Bundle) {
        incomingRequests.add(request)

        allItems = HashMap()

        for (incomingMessage in incomingMessages)
            allItems!![incomingMessage.getString("userID")!!] = incomingMessage.getString("username")!!
        for (incomingRequest in incomingRequests)
            allItems!![incomingRequest.getString("userID")!!] = incomingRequest.getString("username")!!
    }

    fun addMessage(message : Bundle) {
        incomingMessages.add(message)

        allItems = HashMap()

        for (incomingMessage in incomingMessages)
            allItems!![incomingMessage.getString("userID")!!] = incomingMessage.getString("username")!!
        for (incomingRequest in incomingRequests)
            allItems!![incomingRequest.getString("userID")!!] = incomingRequest.getString("username")!!
    }
}
