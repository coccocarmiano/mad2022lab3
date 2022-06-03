package com.example.drawerexample.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawerexample.Message
import com.example.drawerexample.adapter.EditAdvIncomingMessagesAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL

class ChatViewModel : ViewModel() {
    var messages : MutableLiveData<ArrayList<Message>> = MutableLiveData()
    var otherUserProfilePicture : MutableLiveData<Bitmap> = MutableLiveData()
    var otherUserUsername : MutableLiveData<String> = MutableLiveData()

    private var advertisementID : MutableLiveData<String> = MutableLiveData()
    private lateinit var advCreatorID : String
    var otherUserID : MutableLiveData<String> = MutableLiveData()
    private var userID = Firebase.auth.currentUser?.uid
    private val db = Firebase.firestore
    private val storage = Firebase.storage.reference

    init {
        advertisementID.observeForever {
            updateAdvCreatorID()
        }

        otherUserID.observeForever {
            storage.child("users_profile_pictures/${otherUserID.value}").downloadUrl
                .addOnSuccessListener {
                    setOtherProfilePicture(it)
                }
                .addOnFailureListener {
                    Log.w("ChatViewModel", "Error getting profile picture", it)
                }
            db.collection("users").document("${otherUserID.value}").get()
                .addOnSuccessListener { doc ->
                    doc.getString("username").also { otherUserUsername.value = it }
                }
        }

    }

    private fun updateAdvCreatorID() {
        db.collection("advertisements").document("${advertisementID.value}").get()
            .addOnSuccessListener {
                advCreatorID = it.getString("creatorID").toString()
                updateListener()
            }.addOnFailureListener {
                Log.w("ChatViewModel", "Error getting advertisement creatorID", it)
            }
    }

    fun setAdvertisementID(id : String){
        advertisementID.value = id
    }

    @Suppress("UNNECESSARY_NOT_NULL_ASSERTION")
    fun setOtherUserID(id : String?) {
        if ( id != null ) otherUserID.value = id!!
    }

    private fun updateListener() {
        val chatUserID = when ( userID == advCreatorID ) {
            true -> otherUserID.value
            false -> userID
        }

        db
            .collection("chats")
            .document("${advertisementID.value}")
            .collection("$chatUserID")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { doc, err ->
                when ( err == null) {
                    true -> updateCollection(doc)
                    else -> Log.w("ChatViewModel", "Error getting documents.", err)
                }
            }
    }

    private fun updateCollection(doc: QuerySnapshot?) {
        doc?.documents?.map {
            val text = it.getString("text") ?: ""
            val sender = it.getString("sender") ?: ""
            val receiver = it.getString("receiver") ?: ""
            val timestamp = it.getLong("timestamp") ?: 0L
            Message(text, timestamp, sender, receiver)
        }?.toCollection(ArrayList())
            .let {
                messages.value = it
            }
    }

    fun postMessage(text: String) {
        val chatUserID = when (advCreatorID == userID) {
            true -> otherUserID.value
            false -> userID
        }

        if ( userID == null ) return
        if ( otherUserID.value == null ) return

        db
            .collection("chats")
            .document("${advertisementID.value}")
            .collection("$chatUserID")
            .add(
                hashMapOf(
                    "text" to text,
                    "sender" to userID,
                    "receiver" to otherUserID.value,
                    "timestamp" to System.currentTimeMillis()
                )
            ).addOnSuccessListener {
                Log.d("ChatViewModel", "Message posted")
            }.addOnFailureListener {
                Log.w("ChatViewModel", "Error posting message", it)
            }

        db
            .collection("chats")
            .document("${advertisementID.value}")
            .update(mapOf(userID to true)) // Need to keep track of available subcollections
            .addOnFailureListener {
                db.collection("chats")
                    .document("${advertisementID.value}")
                    .set(mapOf(userID to true))
                    .addOnFailureListener {
                        Log.w("ChatViewModel", "Error updating chat", it)
                    }
            }
    }

    private fun setOtherProfilePicture(uri : Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            URL(uri.toString()).let { BitmapFactory.decodeStream(it.openConnection().getInputStream() )}
                .also { viewModelScope.launch { otherUserProfilePicture.value = it } }
        }
    }

    fun sendRequestForAdvertisement( onFailure : () -> Unit = {}, onSuccess : () -> Unit = {}) {
        val junk = Bundle().apply { putString("junk", "junk") }
/*
        db
            .collection("advertisements")
            .document("${advertisementID.value}")
            .collection("requests")
            .document("$userID") // The existence of the document itself is the request flag
            .set(junk)
            .addOnSuccessListener {
                onSuccess()
                Log.d("ChatViewModel", "Request sent")
            }
            .addOnFailureListener { err ->
                Log.w("ChatViewModel", "Error sending request", err)
                onFailure()
            }

 */
        db
            .collection("advertisements")
            .document("${advertisementID.value}")
            .get()
            .addOnSuccessListener {
                val requests = it.get("requests") as? MutableList<String> ?: mutableListOf()
                requests.add("$userID")
                db
                    .collection("advertisements")
                    .document("${advertisementID.value}")
                    .update(
                        hashMapOf(
                            "status" to "pending",
                            "requests" to requests
                        ) as Map<String, Any>
                    ).addOnSuccessListener {
                        onSuccess()
                        Log.d("ChatViewModel", "Request sent")
                    }
                    .addOnFailureListener { err ->
                        Log.w("ChatViewModel", "Error sending request", err)
                        onFailure()
                    }
            }
    }

    fun acceptRequestForAdvertisement(onSuccess : () -> Unit = {}, onFailure : () -> Unit = {}) {
        val payload = mapOf("acceptedFor" to otherUserID.value)
        /*
        db
            .collection("advertisements")
            .document("${advertisementID.value}")
            .collection("requests")
            .document("accepted")
            .set(payload)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { err ->
                Log.w("ChatViewModel", "Error accepting request", err)
                onFailure()
            }
            */

        // TODO find atomic way
        db
            .collection("users")
            .document("${otherUserID.value}")
            .get()
            .addOnSuccessListener { applicantDoc ->
                var applicantCredits = applicantDoc.getLong("credits")
                if (applicantCredits != null && applicantCredits > 0) {
                    applicantCredits -= 1
                    db
                        .collection("users")
                        .document("${otherUserID.value}")
                        .update(
                            hashMapOf(
                                "credits" to applicantCredits
                            ) as Map<String, Any>
                        )

                    db
                        .collection("users")
                        .document("$userID")
                        .get()
                        .addOnSuccessListener {
                            var userCredits = it.getLong("credits")
                            if (userCredits == null)
                                userCredits = 0
                            userCredits += 1
                            db
                                .collection("users")
                                .document("$userID")
                                .update(
                                    hashMapOf(
                                        "credits" to userCredits
                                    ) as Map<String, Any>
                                )
                        }

                    db
                        .collection("advertisements")
                        .document("${advertisementID.value}")
                        .update(
                            hashMapOf(
                                "buyerUID" to otherUserID.value,
                                "status" to "accepted"
                            ) as Map<String, Any>
                        ).addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { err ->
                            Log.w("ChatViewModel", "Error accepting request", err)
                            onFailure()
                        }
                } else {
                    Log.w("ChatViewModel", "Error accepting request", null) // TODO custom exception
                    onFailure()
                }
            }.addOnFailureListener { err ->
                Log.w("ChatViewModel", "Error accepting request", err)
                onFailure()
            }

    }

    fun denyRequestForAdvertisement(onSuccess : () -> Unit = {}, onFailure : () -> Unit = {}) {
        /*
        db
            .collection("advertisements")
            .document("${advertisementID.value}")
            .collection("requests")
            .document("${otherUserID.value}")
            .delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure()
                Log.w("ChatViewModel", "Error denying request", it)
            }
        db
            .collection("advertisements")
            .document("${advertisementID.value}")
            .update(
                hashMapOf(
                    "buyerUID" to "",
                    "status" to ""
                ) as Map<String, Any>
            )

         */

        db
            .collection("advertisements")
            .document("${advertisementID.value}")
            .get()
            .addOnSuccessListener { doc ->
                val requests = doc.get("requests") as? MutableList<String> ?: mutableListOf()
                requests.remove("$userID")
                var status = "pending"
                if (requests.isEmpty())
                    status = ""
                db
                    .collection("advertisements")
                    .document("${advertisementID.value}")
                    .update(
                        hashMapOf(
                            "status" to status,
                            "requests" to requests
                        ) as Map<String, Any>
                    ).addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener {
                        onFailure()
                        Log.w("ChatViewModel", "Error denying request", it)
                    }
            }
    }

    fun didUserRequestTimeSlot(onTrue : () -> Unit = {}, onFalse : () -> Unit = {}) {
        db
            .collection("advertisements")
            .document("${advertisementID.value}")
            .collection("requests")
            .document("$userID")
            .get()
            .addOnSuccessListener {
                if ( it.exists() ) onTrue()
                else onFalse()
            }
            .addOnFailureListener {
                Log.w("ChatViewModel", "Error getting request", it)
            }
    }

    fun didOtherUserRequestTimeSlot(onTrue : () -> Unit = {}, onFalse : () -> Unit = {}) {
        db
            .collection("advertisements")
            .document("${advertisementID.value}")
            .collection("requests")
            .document("${otherUserID.value}")
            .get()
            .addOnSuccessListener {
                when ( it.exists() ) {
                    true -> onTrue()
                    false -> onFalse()
                }
            }
            .addOnFailureListener {
                Log.w("ChatViewModel", "Error getting request", it)
            }
    }

    fun didAdvertiserAcceptRequestForUser(onTrue : () -> Unit = {}, onFalse : () -> Unit = {}) {
        db
            .collection("advertisements")
            .document("${advertisementID.value}")
            .collection("requests")
            .document("accepted")
            .get()
            .addOnSuccessListener { doc ->
                when ( doc.getString("acceptedFor") == userID || doc.getString("acceptedFor") == otherUserID.value ) {
                    true -> onTrue()
                    false -> onFalse()
                }
            }
            .addOnFailureListener {
                Log.w("ChatViewModel", "Error getting request", it)
            }
    }

    fun hasAdvertisementAlreadyBeenAllocated(onTrue : () -> Unit = {}, onFalse : () -> Unit = {}) {
        db
            .collection("advertisements")
            .document("${advertisementID.value}")
            .collection("requests")
            .document("accepted")
            .get()
            .addOnSuccessListener {
                when ( it.exists() ) {
                    true -> onTrue()
                    false -> onFalse()
                }
            }
            .addOnFailureListener {
                Log.w("ChatViewModel", "Error getting request", it)
            }
    }

    fun listenChatsUpdates(advID: String?, adapter : EditAdvIncomingMessagesAdapter) {
        if ( advID == null ) return

        adapter.incomingMessages.clear()

        db
            .collection("chats")
            .document("$advID")
            .addSnapshotListener { allChats, err -> // This document contains a collection for each user chat
                when ( err == null ) {
                    true -> {
                        // First we need to get all the users who sent a message into the chat
                        allChats?.data?.keys?.forEach { userID ->
                            db.collection("users")
                                .document(userID)
                                .get()
                                .addOnSuccessListener {
                                    it.getString("username")?.also { uname ->
                                        val bundle = Bundle().apply {
                                            putString("userID", userID)
                                            putString("username", uname)
                                        }
                                        adapter.incomingMessages.add(bundle)
                                        Log.d("ChatViewModelAdapter", "${adapter.incomingMessages.size}")
                                        adapter.notifyDataSetChanged()
                                    }
                                }
                        }
                    }
                    else -> {
                        Log.w("ChatViewModel", "Error getting documents.", err)
                    }
                }
            }
    }


}