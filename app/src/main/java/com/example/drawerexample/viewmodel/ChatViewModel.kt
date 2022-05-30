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
    }

    private fun setOtherProfilePicture(uri : Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            URL(uri.toString()).let { BitmapFactory.decodeStream(it.openConnection().getInputStream() )}
                .also { viewModelScope.launch { otherUserProfilePicture.value = it } }
        }
    }

    fun sendRequestForAdvertisement( onFailure : () -> Unit = {}, onSuccess : () -> Unit = {}) {
        val junk = Bundle().apply { putString("junk", "junk") }
        db
            .collection("advertisements")
            .document("${advertisementID.value}")
            .collection("requests")
            .document("${otherUserID.value}") // The existence of the document itself is the request flag
            .set(junk)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { err ->
                Log.w("ChatViewModel", "Error sending request", err)
                onFailure()
            }
    }

    fun acceptRequestForAdvertisement() {
        val payload = Bundle().apply { putString("acceptedFor", otherUserID.value) }
        db
            .collection("advertisements")
            .document("${advertisementID.value}")
            .collection("requests")
            .document("accepted")
            .set(payload)
            .addOnFailureListener { err ->
                Log.w("ChatViewModel", "Error accepting request", err)
            }
    }

    fun denyRequestForAdvertisement() {
        db
            .collection("advertisements")
            .document("${advertisementID.value}")
            .collection("requests")
            .document("$otherUserID.value")
            .delete()
            .addOnFailureListener {
                Log.w("ChatViewModel", "Error denying request", it)
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


}