package com.example.drawerexample.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.drawerexample.Advertisement
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class AdvertisementViewModel(val app : Application): AndroidViewModel(app) {

    var liveAdvList = MutableLiveData<MutableList<Advertisement>>()
    private val db = FirebaseFirestore.getInstance()

    init {
        db.collection("advertisements")
            .addSnapshotListener { changes, err ->
                when (err) {
                    null -> {
                        liveAdvList.value =
                            changes?.documents?.map { doc -> doc.toAdvertisement() } as MutableList<Advertisement>
                    }
                    else -> Log.w("Firebase", "Error receiving updates", err)
                }
            }

    }


    fun createAdvertisement(adv: Advertisement) {
        db.collection("advertisements")
            .document()
            .set(
                hashMapOf(
                    "title" to adv.title,
                    "description" to adv.description,
                    "location" to adv.location,
                    "date" to adv.date,
                    "duration" to adv.duration,
                    "skill" to adv.skill,
                    "creatorMail" to adv.creatorMail,
                    "creatorID" to adv.creatorUID,
                    "buyerUID" to adv.buyerUID,
                    "status" to adv.status,
                    "requests" to listOf<String>()
                )
            ).addOnFailureListener {
                Log.d("Firebase", it.toString())
            }
    }

    @Suppress("UNCHECKED_CAST")
    fun updateAdvertisement(id: String, adv: Advertisement) {
        db.collection("advertisements").document(id)
            .update( hashMapOf(
                "title" to adv.title,
                "description" to adv.description,
                "location" to adv.location,
                "date" to adv.date,
                "duration" to adv.duration,
                "skill" to adv.skill,
                "emailCreator" to adv.creatorMail,
                "buyerUID" to adv.buyerUID,
                "status" to adv.status,

                "rateForBuyer" to adv.rateForBuyer,
                "rateForSeller" to adv.rateForSeller,
                "commentForBuyer" to adv.commentForBuyer,
                "commentForSeller" to adv.commentForSeller,
            ) as MutableMap<String, Any>
        ).addOnFailureListener {
            Log.w("Firebase", "Could not upadte advertisement", it)
        }
    }

    private fun DocumentSnapshot.toAdvertisement(): Advertisement {
        return Advertisement().also { adv ->
            adv.id = id
            adv.title = getString("title") ?: "No Title"
            adv.description = getString("description") ?: "No Description"
            adv.location = getString("location") ?: "No Location"
            adv.date = getString("date") ?: "No Date"
            adv.duration = getString("duration") ?: "No Duration"
            adv.skill = getString("skill") ?: "No Skill"
            adv.creatorMail = getString("emailCreator") ?: "No creator"
            adv.creatorUID = getString("creatorID") ?: "No creator UID"
            adv.buyerUID = getString("buyerUID") ?: "No buyer UID"
            adv.status = getString("status") ?: "No status"
            adv.requests = get("requests") as? List<String> ?: listOf()

            adv.rateForBuyer = getDouble("rateForBuyer")?.toFloat()
            adv.rateForSeller = getDouble("rateForSeller")?.toFloat()
            adv.commentForBuyer = getString("commentForBuyer")
            adv.commentForSeller = getString("commentForSeller")
        }
    }

}
