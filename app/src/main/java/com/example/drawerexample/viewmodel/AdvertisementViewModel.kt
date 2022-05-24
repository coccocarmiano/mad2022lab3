package com.example.drawerexample.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.drawerexample.Advertisement
import com.example.drawerexample.UserProfile
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class AdvertisementViewModel(val app : Application): AndroidViewModel(app) {

    var liveAdvList = MutableLiveData<MutableList<Advertisement>>()
    val creatorToShow: MutableLiveData<UserProfile> = MutableLiveData()
    private val db = FirebaseFirestore.getInstance()
    val userID : MutableLiveData<String?> = MutableLiveData(null);

    init {
        db.collection("advertisements")
            .addSnapshotListener { changes, err ->
                when (err) {
                    null -> {
                        liveAdvList.value =
                            changes?.documents?.map { doc -> doc.toAdvertisement() } as MutableList<Advertisement>
                    }
                    else -> Toast.makeText(app, "Firestore $err", Toast.LENGTH_LONG).show()
                }
            }
    }


    fun findCreator(email: String) {
        db.collection("users").whereEqualTo("email" , email).get()
            .addOnSuccessListener {
                for (doc in it) {
                    creatorToShow.value = doc.toCreator()
                    userID.value = doc.id
            }
        }.addOnFailureListener{
            it.printStackTrace()
            }
    }

    fun saveAdvertisement(adv: Advertisement) {
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
                    "emailCreator" to adv.emailCreator
                )
            ).addOnFailureListener {
                Log.d("Firebase", it.toString())
            }
    }

    fun updateAdvertisement(id: String, adv: Advertisement) {
        db.collection("advertisements").document(id).set(
            hashMapOf(
                "title" to adv.title,
                "description" to adv.description,
                "location" to adv.location,
                "date" to adv.date,
                "duration" to adv.duration,
                "skill" to adv.skill,
                "emailCreator" to adv.emailCreator
            )
        ).addOnFailureListener {
            Log.d("Firebase", it.toString())
        }
    }

    private fun DocumentSnapshot.toCreator(): UserProfile {
        return UserProfile().also { user ->
            user.fullname = getString("fullname") ?: "No Fullname"
            user.mail = getString("email") ?: "No Email"
            user.location = getString("location") ?: "No Location"
            user.skills = get("skills") as? List<String> ?: listOf()
            user.username = getString("username") ?: "No Username"
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
            adv.emailCreator = getString("emailCreator") ?: "No creator"
        }
    }

}
