package com.example.drawerexample.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drawerexample.Message
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatViewModel : ViewModel() {
    var messages : MutableLiveData<ArrayList<Message>> = MutableLiveData()
    private var advertisementID : MutableLiveData<String> = MutableLiveData()
    private var advertiserID : String? = null
    private var otherUserID : String? = null
    private val db = Firebase.firestore

    init {
        advertisementID.observeForever {
            updateAdvertiserID()
            updateListener()
        }
    }

    fun applyAdvertisementID(id : String){
        advertisementID.value = id
    }

    fun applyRequesterID(id : String){
        advertiserID = id
    }

    private fun updateAdvertiserID() {
        db.collection("advertisements").document("${advertisementID.value}").get().addOnSuccessListener {
            advertiserID = it.getString("creatorID")
        }
    }

    private fun updateListener() {
        db
            .collection("chats")
            .document("${advertisementID.value}")
            .collection("$otherUserID")
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

    private fun postMessage(text: String, sender : String) {
        val receiver = when (sender) {
            advertiserID -> otherUserID
            else -> advertiserID
        }

        db
            .collection("chats")
            .document("${advertisementID.value}")
            .collection("$otherUserID")
            .add(
                hashMapOf(
                    "text" to text,
                    "sender" to sender,
                    "receiver" to receiver,
                    "timestamp" to System.currentTimeMillis()
                )
            )
    }
}