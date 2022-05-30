package com.example.drawerexample.ui

import android.animation.LayoutTransition
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.drawerexample.databinding.FragmentChatBinding
import com.example.drawerexample.viewmodel.ChatViewModel

class ChatFragment : Fragment() {
    private val chatViewModel : ChatViewModel by viewModels()
    private lateinit var binding: FragmentChatBinding
    private var userID : String? = null
    private var advertisementID : String? = null
    private var otherUserID : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        advertisementID = arguments?.getString("advertisementID")
        userID = arguments?.getString("userID")
        otherUserID = arguments?.getString("otherUserID")

        chatViewModel.run {
            userID?.also { setAdvertisementID(it) }
            otherUserID?.also { setOtherUserID(it) }
            advertisementID?.also { setAdvertisementID(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        val root = binding.root

        listenOtherUserProfile()


        // Enable animations
        binding.chatMessagesRecyclerViewContainer.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        return root
    }

    private fun listenOtherUserProfile() {
        chatViewModel.otherUserProfilePicture.observe(viewLifecycleOwner) {
            binding.chatUserProfilePictureImageView.setImageBitmap(it)
        }

        chatViewModel.otherUserUsername.observe(viewLifecycleOwner) {
            binding.chatUserDisplayedName.text = it
        }
    }

}