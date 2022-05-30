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
    private var advertiserID : String? = null
    private var advertisementID : String? = null
    private var requesterID : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        val root = binding.root
        advertisementID = arguments?.getString("advertisementID")
        advertiserID = arguments?.getString("advertiserID")
        requesterID = arguments?.getString("requesterID")

        chatViewModel.run {
            advertiserID?.also { applyAdvertisementID(it) }
            requesterID?.also { applyRequesterID(it) }
        }

        // Enable animations
        binding.chatMessagesRecyclerViewContainer.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

        return root
    }

}