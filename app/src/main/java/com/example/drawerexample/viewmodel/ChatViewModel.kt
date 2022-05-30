package com.example.drawerexample.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drawerexample.Message
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
    private var otherUserID : MutableLiveData<String> = MutableLiveData()
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
        db.collection("advertisements").document("$advertisementID").get()
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

    fun setOtherUserID(id : String) {
        otherUserID.value = id
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

    private fun postMessage(text: String, sender : String) {
        val chatUserID = when (advCreatorID == userID) {
            true -> otherUserID.value
            false -> userID
        }

        val receiver = when ( sender == advCreatorID ) {
            true -> otherUserID.value
            false -> advCreatorID
        }

        db
            .collection("chats")
            .document("${advertisementID.value}")
            .collection("$chatUserID")
            .add(
                hashMapOf(
                    "text" to text,
                    "sender" to sender,
                    "receiver" to receiver,
                    "timestamp" to System.currentTimeMillis()
                )
            )
    }

    private fun setOtherProfilePicture(uri : Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            URL(uri.toString()).let { BitmapFactory.decodeStream(it.openConnection().getInputStream() )}
                .also { viewModelScope.launch { otherUserProfilePicture.value = it } }
        }
    }

}