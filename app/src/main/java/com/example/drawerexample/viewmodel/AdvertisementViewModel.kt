package com.example.drawerexample.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drawerexample.Advertisement
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class AdvertisementViewModel(): ViewModel() {

    var liveAdvList = MutableLiveData<MutableList<Advertisement>>()
    private var listenerRegistration : ListenerRegistration
    private val db = FirebaseFirestore.getInstance()

    init {
        listenerRegistration = db.collection("advertisements")
            .addSnapshotListener { v, e ->
                if (e==null) {
                    liveAdvList.value=(v!!.mapNotNull { d -> d.toAdvertisement() } as MutableList<Advertisement>)
                }
            }
        liveAdvList=liveAdvList
    }

    fun saveAdvertisement(adv : Advertisement) {
        db.collection("advertisements")
            .document()
            .set(
                hashMapOf(
                "title" to adv.title,
                "description" to adv.description,
                "location" to adv.location,
                "date" to adv.date,
                "duration" to adv.duration
            ))
            .addOnSuccessListener { it -> Log.d("Firebase", "success ${it.toString()}") }
            .addOnFailureListener { Log.d("Firebase", it.message ?: "Error") }

    }

    fun updateAdvertisement(id:String, adv : Advertisement) {
        db.collection("advertisements").document(id).set(
            hashMapOf(
                "title" to adv.title,
                "description" to adv.description,
                "location" to adv.location,
                "date" to adv.date,
                "duration" to adv.duration
            )
        )
            .addOnSuccessListener { it -> Log.d("Firebase", "success ${it.toString()}") }
            .addOnFailureListener { Log.d("Firebase", it.message ?: "Error") }

    }

    override fun onCleared() {

        super.onCleared()
        listenerRegistration.remove()
    }

    private fun DocumentSnapshot.toAdvertisement(): Advertisement {
        return Advertisement().also {
            it.id = id
            it.title = get("title") as String
            it.description = get("description") as String
            it.date = get("date") as String
            it.duration = get("duration") as String
            it.location = get("location") as String
        }
    }

}
