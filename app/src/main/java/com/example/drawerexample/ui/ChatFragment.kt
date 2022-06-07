package com.example.drawerexample.ui

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawerexample.NotEnoughCreditsException
import com.example.drawerexample.R
import com.example.drawerexample.adapter.ChatMessagesAdapter
import com.example.drawerexample.databinding.FragmentChatBinding
import com.example.drawerexample.viewmodel.ChatViewModel
import com.google.android.material.snackbar.Snackbar

class ChatFragment : Fragment() {
    private val chatViewModel : ChatViewModel by viewModels()
    private lateinit var binding: FragmentChatBinding
    private var userID : String? = null
    private var advertisementID : String? = null
    private var otherUserID : String? = null
    private val chatMessagesAdapter = ChatMessagesAdapter(this)
    private var userIsAdvertiser = false
    private var msgCnt = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        advertisementID = arguments?.getString("advertisementID")
        userID = arguments?.getString("userID")
        otherUserID = arguments?.getString("otherUserID")
        userIsAdvertiser = arguments?.getBoolean("userIsAdvertiser") ?: false

        otherUserID?.also { chatViewModel.setOtherUserID(it)}
        advertisementID?.also { chatViewModel.setAdvertisementID(it) }

        chatMessagesAdapter.messages = chatViewModel.messages.value ?: ArrayList()
        msgCnt = savedInstanceState?.getInt("msg_count") ?: 0
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
        binding.chatWriteMessageTextInput.setText("")

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chatSendMessageButton.setOnClickListener { sendMessage() }
        manageSendRequestPopup()
        managePendingRequestPopup()
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
        if ( chatMessagesAdapter.itemCount == msgCnt ) return;
        val layoutManager = binding.chatMessagesRecyclerView.layoutManager as LinearLayoutManager
        val firstVisible = layoutManager.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()

        @SuppressLint("NotifyDataSetChanged")
        if (lastVisible > chatMessagesAdapter.messages.size) {
            chatMessagesAdapter.notifyDataSetChanged()
            return
        }

        chatMessagesAdapter.notifyItemRangeChanged(firstVisible, lastVisible)
        chatMessagesAdapter.notifyItemInserted(0)
        binding.chatMessagesRecyclerView.scrollToPosition(0)
    }

    private fun listenForMessages() {
        chatViewModel.messages.observeForever {
            if ( it.size != chatMessagesAdapter.itemCount ) {
                chatMessagesAdapter.messages = it
                smoothlyAnimateMessages()
            }
        }
    }

    private fun manageSendRequestPopup() {

        if (userIsAdvertiser) return

        val chatPopup = binding.chatPopupSendRequest

        val showSendRequestPopup : () -> Unit = {
                chatPopup.root.animation = AnimationUtils.loadAnimation(this.context, R.anim.slide_up_down)
                chatPopup.root.visibility = View.VISIBLE
                chatPopup.requestTimeSlotButton.isClickable = true
        }

        val disableSendRequestPopup : () -> Unit = {
            val text = getString(R.string.time_slot_already_requested)
            chatPopup.root.animation = AnimationUtils.loadAnimation(this.context, R.anim.slide_up_down)
            chatPopup.root.visibility = View.VISIBLE
            chatPopup.requestTimeSlotButton.visibility = View.GONE
            chatPopup.requestTimeSlotTextView.text = text
        }

        val setAcceptedRequestPopup : () -> Unit = {
            val text = getString(R.string.time_slot_accepted)
            chatPopup.root.animation = AnimationUtils.loadAnimation(this.context, R.anim.slide_up_down)
            chatPopup.root.visibility = View.VISIBLE
            chatPopup.requestTimeSlotButton.visibility = View.GONE
            chatPopup.requestTimeSlotTextView.text = text
        }

        val errRequestingTimeslot = {
            chatPopup.requestTimeSlotButton.isClickable = true
            showSnackBarError("Error requesting the timeslot")
        }

        chatViewModel.didUserRequestTimeSlot(onTrue = disableSendRequestPopup, onFalse = showSendRequestPopup)
        chatViewModel.didAdvertiserAcceptRequestForUser(onTrue = setAcceptedRequestPopup)

        chatPopup.requestTimeSlotButton.setOnClickListener {
            chatPopup.requestTimeSlotButton.isClickable = false
            chatViewModel.sendRequestForAdvertisement( onSuccess = disableSendRequestPopup, onFailure = errRequestingTimeslot )
        }
    }


    private fun showRequestAcceptedPopup() {
        val chatPopup = binding.chatPopupPendingRequest
        chatPopup.root.animation = AnimationUtils.loadAnimation(this.context, R.anim.slide_up_down)
        chatPopup.root.visibility = View.VISIBLE
        chatPopup.requestPendingTimeSlotControls.visibility = View.GONE
        chatPopup.requestPendingTimeSlotTextView.text = getString(R.string.time_slot_accepted_for_advertiser)
    }

    private fun managePendingRequestPopup(){

        if (!userIsAdvertiser) return;

        val chatPopup = binding.chatPopupPendingRequest

        val showRequestPendingPopup : () -> Unit = {
            chatPopup.root.animation = AnimationUtils.loadAnimation(this.context, R.anim.slide_up_down)
            chatPopup.root.visibility = View.VISIBLE
            activateAcceptDenyButtons()
        }

        val accept : () -> Unit = { showRequestAcceptedPopup() }

        chatViewModel.didOtherUserRequestTimeSlot(onTrue = showRequestPendingPopup)
        chatViewModel.didAdvertiserAcceptRequestForUser(onTrue = accept)
    }

    private fun activateAcceptDenyButtons() {
        val chatPopup = binding.chatPopupPendingRequest
        val accept : () -> Unit = { showRequestAcceptedPopup() }
        val hidePopup : () -> Unit = {
            chatPopup.root.animation = AnimationUtils.loadAnimation(this.context, R.anim.slide_down_up)
            chatPopup.root.visibility = View.GONE
        }
        val onFailedAcceptance : (ex : Exception) -> Unit = {
            if (it is NotEnoughCreditsException) {
                showSnackBarError("Acceptance failed, the buyer has 0 credits")
            } else {
                showSnackBarError("Acceptance failed, try again later")
            }
        }

        chatPopup.requestPendingTimeSlotAcceptButton.setOnClickListener {
            chatViewModel.acceptRequestForAdvertisement(onSuccess = accept, onFailure = onFailedAcceptance)
        }

        chatPopup.requestPendingTimeSlotDenyButton.setOnClickListener {
            chatViewModel.denyRequestForAdvertisement(onSuccess = hidePopup)
        }
    }


    private fun showSnackBarError(text : String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_LONG).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("msg_count", chatMessagesAdapter.itemCount)
    }

}