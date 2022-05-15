package com.example.drawerexample.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.drawerexample.Advertisement
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class AdvertisementViewModel: ViewModel() {

    var liveAdvList = MutableLiveData<MutableList<Advertisement>>()
    private val db = FirebaseFirestore.getInstance()

    init {
        db.collection("advertisements")
            .addSnapshotListener { changes, err ->
                when (err) {
                    null -> {
                        liveAdvList.value = changes?.map { doc -> doc.toAdvertisement() } as MutableList<Advertisement>
                    }
                    else -> Log.e("Firestore", err.toString())
                }
            }
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
                )
            ).addOnFailureListener {
                Log.d("Firebase", it.toString())
            }

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
        ).addOnFailureListener {
            Log.d("Firebase", it.toString())
        }

    }


    private fun DocumentSnapshot.toAdvertisement(): Advertisement {
        return Advertisement().also { adv ->
            adv.id = getString("id") ?: "GARBAGE_ID"
            adv.title = getString("title") ?: "No Title"
            adv.description = getString("description") ?: "No Description"
            adv.location = getString("location") ?: "No Location"
            adv.date = getString("date") ?: "No Date"
            adv.duration = getString("duration") ?: "No Duration"
        }
    }

}
