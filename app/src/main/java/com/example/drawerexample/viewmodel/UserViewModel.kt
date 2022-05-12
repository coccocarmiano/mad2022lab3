package com.example.drawerexample.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.drawerexample.R
import com.example.drawerexample.UserProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.io.File

class UserViewModel(private val app: Application) : AndroidViewModel(app) {

    @Suppress("unused")
    constructor(test : String) : this(Application()) {
        if (!userDocumentExists()) createDefaultUserDocument()

        userDocumentReference.addSnapshotListener {  doc, err -> run {
            when (err) {
                null -> doc?.run { updateUserFromDocument(this) }
                else -> Log.e("UserViewModel", err.toString())
            }
        }
        }
    }

    val liveUser = MutableLiveData<UserProfile>()
    val livePicture = MutableLiveData<Bitmap>()
    val liveSkills = MutableLiveData<MutableList<String>>()
    private val uid = Firebase.auth.currentUser?.uid ?: "NULL_USER_UID"
    private val db = FirebaseFirestore.getInstance()
    private val userDocumentReference = db.collection("users").document(uid)


    fun storeProfilePicture(bmp : Bitmap) {
        val user = liveUser.value!!

        bmp.compress(Bitmap.CompressFormat.PNG, 100, app.openFileOutput(user.profilePictureFilename, Context.MODE_PRIVATE))
        livePicture.value = loadProfilePicture(user.profilePictureFilename)
    }

    private fun loadProfilePicture(profilePictureFileName : String) : Bitmap {
        return File(app.filesDir, profilePictureFileName)
            .run {
                when (exists()) {
                    true -> BitmapFactory.decodeFile(File(app.filesDir, profilePictureFileName).absolutePath)
                    false -> BitmapFactory.decodeResource(app.resources, R.drawable.default_pfp)
                }
            }

    }

    fun updateProfilePictureFromURI(uri : Uri) {
        val user = liveUser.value!!

        val inStream = app.contentResolver.openInputStream(uri)
        val outStream = File(app.filesDir, user.profilePictureFilename).outputStream()
        inStream?.also {
            it.copyTo(outStream)
        }
        livePicture.value = loadProfilePicture(user.profilePictureFilename)
    }

    private fun userDocumentExists(): Boolean {
        return userDocumentReference.get().result.exists()
    }

    private fun createDefaultUserDocument() {
        val userHashMap = hashMapOf(
            "fullName" to app.getString(R.string.fullname_placeholder_text),
            "email" to app.getString(R.string.email_placeholder_text),
            "location" to app.getString(R.string.location_placeholder_text),
            "skills" to listOf<String>()
        )

        userDocumentReference.set(userHashMap).addOnFailureListener {
            Log.e("UserViewModel", "Failed to create default user document")
        }
    }

    private fun updateUserFromDocument(doc : DocumentSnapshot) {
        val user = liveUser.value
        user?.run {
            doc.getString("fullName")?.let {
                fullname = it
            }
            doc.getString("email")?.let {
                mail = it
            }
            doc.getString("location")?.let {
                location = it
            }
            doc.get("skills")?.let {
                try {
                    @Suppress("UNCHECKED_CAST")
                    skills = it as List<String>
                } catch (e : ClassCastException) {
                    Log.e("UserViewModel", "Failed to cast skills to list")
                }
            }
        }
    }

}

