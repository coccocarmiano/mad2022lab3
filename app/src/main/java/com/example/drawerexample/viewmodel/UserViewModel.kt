package com.example.drawerexample.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.drawerexample.R
import com.example.drawerexample.UserProfile
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.net.URL
import java.nio.ByteBuffer

class UserViewModel(val app : Application) : AndroidViewModel(app) {

    val liveUser : MutableLiveData<UserProfile> = MutableLiveData()
    val livePicture = MutableLiveData<Bitmap>()
    private val uid = Firebase.auth.currentUser?.uid ?: "NULL_USER_UID"
    private val db = FirebaseFirestore.getInstance()
    private val userDocumentReference = db.collection("users").document(uid)

    init {
        liveUser.value = UserProfile()
        userDocumentReference.get()
            .addOnSuccessListener {
                if (!it.exists()) createDefaultUserDocument()
                else updateUserFromDocument(it)
            }.addOnFailureListener {
                Toast.makeText(app, "Failed to get user document", Toast.LENGTH_LONG).show()
            }

        attachUserInfoListener()
        setUserPhoto()
    }


    private fun createDefaultUserDocument() {
        val currentUser = Firebase.auth.currentUser
        val userHashMap = hashMapOf(
            "fullName" to  ( currentUser?.displayName ?: app.getString(R.string.username_placeholder_text) ),
            "username" to app.getString(R.string.username_placeholder_text),
            "email" to ( currentUser?.email ?: app.getString(R.string.email_placeholder_text) ),
            "location" to app.getString(R.string.location_placeholder_text),
            "skills" to listOf<String>(),
            "pfpProg" to 0,
        )

        userDocumentReference.set(userHashMap).addOnFailureListener {
            Log.e("UserViewModel", "Failed to create default user document")
        }

    }


    private fun updateUserFromDocument(doc : DocumentSnapshot) {
        UserProfile().apply {
            doc.getString("fullName")?.let {
                fullname = it
            }
            doc.getString("username")?.let {
                username = it
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
                } catch (e: ClassCastException) {
                    Log.e("UserViewModel", "Failed to cast skills to list")
                }
            }
        }.also {
            liveUser.value = it
        }
    }

    private fun pushChangesToFirebase() {
        liveUser.value?.run {
            val userHashMap = hashMapOf(
                "fullName" to fullname,
                "email" to mail,
                "location" to location,
                "skills" to skills,
                "username" to username,
            )
            userDocumentReference.set(userHashMap)
        }
    }

    fun updateSkills(skills : List<String>) {
        liveUser.value?.skills = skills
        pushChangesToFirebase()

        //  triggering observer of liveUser
        liveUser.value = liveUser.value
    }

    fun updateViewModel() {
        liveUser.value = UserProfile(liveUser.value ?: UserProfile())
        pushChangesToFirebase()
    }

    private fun attachUserInfoListener() {
        userDocumentReference.addSnapshotListener { doc, err ->
            run {
                when (err) {
                    null -> doc?.run { updateUserFromDocument(this) }
                    else -> Log.e("UserViewModel", err.toString())
                }
            }
        }
    }

    private fun setUserPhoto() {
        setPhotoFromFirebase()
    }

    private fun setPhotoFromGoogle() {
        when (val photoURI = Firebase.auth.currentUser?.photoUrl) {
            null -> {
                setDefaultPhoto()
            }
            else -> {
                setProfilePictureFromURI(photoURI)
            }
        }
    }

    private fun setPhotoFromFirebase() {
        Firebase.storage.reference
            .child("users_profile_pictures")
            .child(uid)
            .downloadUrl
            .addOnSuccessListener {
                setProfilePictureFromURI(it)
            }.addOnFailureListener {
                setPhotoFromGoogle()
            }
    }

    private fun setProfilePictureFromURI(photoURI : Uri) {
        Thread {
            URL(photoURI.toString()).openStream().use {
                val bitmap = BitmapFactory.decodeStream(it)
                livePicture.postValue(bitmap)
                uploadPhotoToFirebase(bitmap)
            }
        }.start()
    }

    private fun setDefaultPhoto() {
        val bmp = BitmapFactory.decodeResource(app.resources, R.drawable.default_pfp)
        livePicture.value = bmp
        uploadPhotoToFirebase(bmp)
    }

    private fun uploadPhotoToFirebase(bmp : Bitmap) {
        val byteOutStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteOutStream)
        val byteArray = byteOutStream.toByteArray()
        Firebase.storage.reference
            .child("users_profile_pictures")
            .child(uid)
            .putBytes(byteArray)
            .addOnFailureListener {
                Toast.makeText(app, "Failed to upload photo: ${it.toString()}", Toast.LENGTH_SHORT).show()
            }
    }

    fun updateProfilePictureFromBitmap(bmp : Bitmap) {
        livePicture.postValue(bmp)
        uploadPhotoToFirebase(bmp)
    }

    fun updateProfilePictureFromURI(uri : Uri) {
        setProfilePictureFromURI(uri)
    }
}

