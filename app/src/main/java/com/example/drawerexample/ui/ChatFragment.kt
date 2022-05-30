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
    private var firstUpdate = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        advertisementID = arguments?.getString("advertisementID")
        userID = arguments?.getString("userID")
        otherUserID = arguments?.getString("otherUserID")

        otherUserID?.also { chatViewModel.setOtherUserID(it)}
        advertisementID?.also { chatViewModel.setAdvertisementID(it) }

        chatMessagesAdapter = ChatMessagesAdapter(this)
        chatMessagesAdapter.messages = chatViewModel.messages.value ?: ArrayList()

        firstUpdate = true
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
        binding.chatMessagesRecyclerView.layoutManager = LinearLayoutManager(context).apply { reverseLayout = true }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val enableRequestButton : () -> Unit = { binding.chatPopupSendRequest.requestTimeSlotButton.isClickable = true }
        val hideRequestPopup : () -> Unit = { binding.chatPopupSendRequest.root.visibility = View.GONE }
        val showRequestPopup : () -> Unit = { binding.chatPopupSendRequest.root.visibility = View.VISIBLE }

        binding.chatSendMessageButton.setOnClickListener { sendMessage() }

        binding.chatPopupSendRequest.requestTimeSlotButton.setOnClickListener {
            binding.chatPopupSendRequest.requestTimeSlotButton.isClickable = false
            chatViewModel.sendRequestForAdvertisement(onFailure = enableRequestButton, onSuccess = hideRequestPopup)
        }
        chatViewModel.didUserRequestTimeSlot(onTrue = hideRequestPopup, onFalse = showRequestPopup)
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
        val textToSend = binding.chatWriteMessageTextInput.text.toString() // TODO: strip message?
        if (textToSend.isBlank()) return
        chatViewModel.postMessage(textToSend)
        binding.chatWriteMessageTextInput.text?.clear()
    }

    private fun smoothlyAnimateMessages() {
        val layoutManager = binding.chatMessagesRecyclerView.layoutManager as LinearLayoutManager
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()

        if (lastVisible > chatMessagesAdapter.messages.size) {
            chatMessagesAdapter.notifyDataSetChanged()
            return
        }

        chatMessagesAdapter.notifyItemRangeChanged(firstVisible, firstVisible)
        chatMessagesAdapter.notifyItemInserted(0)
        layoutManager.scrollToPosition(0)
    }

    private fun listenForMessages() {
        chatViewModel.messages.observe(viewLifecycleOwner) {
            chatMessagesAdapter.messages = it
            when (chatMessagesAdapter.messages.size) {
                0 -> chatMessagesAdapter.notifyDataSetChanged()
                1 -> chatMessagesAdapter.notifyItemInserted(0)
                else -> smoothlyAnimateMessages()
            }

        }
    }

}