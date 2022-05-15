package com.example.drawerexample.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drawerexample.Advertisement
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class AdvertisementViewModel(): ViewModel() {

    var liveAdvList = MutableLiveData<MutableList<Advertisement>>()
    private var l:ListenerRegistration
    private val db = FirebaseFirestore.getInstance()

    init {
        l = db.collection("advertisements")
            .addSnapshotListener { v, e ->
                if (e==null) {
                    liveAdvList.value=(v!!.mapNotNull { d -> d.toAdvertisement() } as MutableList<Advertisement>)
                }
            }
        liveAdvList=liveAdvList
    }

    fun save(adv : Advertisement) {
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
        l.remove()
    }

    fun DocumentSnapshot.toAdvertisement(): Advertisement? {
        return try {
            val id = id
            val title = get("title") as String
            val description = get("description") as String
            val date = get("date") as String
            val duration = get("duration") as String
            val location = get("location") as String

            Advertisement(id, title, description, date, duration, location)
        } catch (e: Exception) {
            e.printStackTrace()
        null
    }
}

}
