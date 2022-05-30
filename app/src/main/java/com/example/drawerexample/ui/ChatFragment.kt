package com.example.drawerexample.ui

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawerexample.adapter.ChatMessagesAdapter
import com.example.drawerexample.databinding.FragmentChatBinding
import com.example.drawerexample.viewmodel.ChatViewModel

class ChatFragment : Fragment() {
    private val chatViewModel : ChatViewModel by viewModels()
    private lateinit var binding: FragmentChatBinding
    private var userID : String? = null
    private var advertisementID : String? = null
    private var otherUserID : String? = null
    private lateinit var chatMessagesAdapter : ChatMessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        advertisementID = arguments?.getString("advertisementID")
        userID = arguments?.getString("userID")
        otherUserID = arguments?.getString("otherUserID")

        otherUserID?.also { chatViewModel.setOtherUserID(it)}
        advertisementID?.also { chatViewModel.setAdvertisementID(it) }

        chatMessagesAdapter = ChatMessagesAdapter(this)
        chatMessagesAdapter.messages = chatViewModel.messages.value ?: ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        val root = binding.root

        listenOtherUserProfile()

        listenForMessages()

        // Enable animations
        binding.chatMessagesRecyclerViewContainer.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        binding.chatMessagesRecyclerView.adapter = chatMessagesAdapter
        (binding.chatMessagesRecyclerView.layoutManager as LinearLayoutManager).let {
            it.reverseLayout = true
            it.stackFromEnd = true
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.chatSendMessageButton.setOnClickListener { sendMessage() }
    }

    private fun listenOtherUserProfile() {
        chatViewModel.otherUserProfilePicture.observe(viewLifecycleOwner) {
            binding.chatUserProfilePictureImageView.setImageBitmap(it)
        }

        chatViewModel.otherUserUsername.observe(viewLifecycleOwner) {
            binding.chatUserDisplayedName.text = it
        }
    }

    private fun sendMessage() {
        val textToSend = binding.chatWriteMessageTextInput.text.toString()
        chatViewModel.postMessage(textToSend)
    }

    private fun listenForMessages() {
        chatViewModel.messages.observe(viewLifecycleOwner) {
            chatMessagesAdapter.messages = it
            chatMessagesAdapter.notifyDataSetChanged()
        }
    }

}