package com.example.lab3

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.edit_profile_fragment.view.*
import org.json.JSONObject
import java.io.File


class UserViewModel(private val app: Application) : AndroidViewModel(app) {

    val fullname = MutableLiveData<String>()
    val username = MutableLiveData<String>()
    val location = MutableLiveData<String>()
    val mail = MutableLiveData<String>()
    val profilePictureBitmap = MutableLiveData<Bitmap>()
    private val profilePictureFileName = "profilePicture.png"
    val skills = MutableLiveData<MutableList<String>>()

    init {
        val jsonString = app.getSharedPreferences("user", Context.MODE_PRIVATE).getString("user", "{}") ?: "{}"
        val json = JSONObject(jsonString)

        json.apply {
            fullname.value = optString("fullname", app.getString(R.string.fullname_placeholder_text))
            username.value = optString("username", app.getString(R.string.username_placeholder_text))
            location.value = optString("location", app.getString(R.string.location_placeholder_text))
            mail.value = optString("mail", app.getString(R.string.email_placeholder_text))
            skills.value = optString("skills", "").let {
                when (it) {
                    "" -> mutableListOf()
                    else -> it.split(",").toMutableList()
                }
            }
        }



        loadProfilePicture()
    }

    private fun save() {
        JSONObject()
            .apply {
                put("fullname", fullname.value)
                put("username", username.value)
                put("location", location.value)
                put("mail", mail.value)
                put("skills", skills.value?.joinToString(",") ?: "")
            }.toString()
            .run {
                 app
                .getSharedPreferences("user", Context.MODE_PRIVATE)
                .edit()
                .putString("user", this)
                .apply()
            }

    }

    fun storeProfilePicture(bmp : Bitmap) {
        bmp.compress(Bitmap.CompressFormat.PNG, 100, app.openFileOutput(profilePictureFileName, Context.MODE_PRIVATE))
        loadProfilePicture()
    }

    private fun loadProfilePicture() : Bitmap {
        return File(app.filesDir, profilePictureFileName)
            .run {
                when (exists()) {
                    true -> BitmapFactory.decodeFile(File(app.filesDir, profilePictureFileName).absolutePath)
                    false -> BitmapFactory.decodeResource(app.resources, R.drawable.default_pfp)
                }
            }.also {
                profilePictureBitmap.value = it
            }

    }

    fun updateFromEditProfile(view: View){
        fullname.value = view.fullNameET.text.toString()
        mail.value = view.emailET.text.toString()
        location.value = view.locationET.text.toString()
        username.value = view.usernameET.text.toString()
        save()
    }


    fun updateProfilePictureFromURI(uri : Uri) {
        val inStream = app.contentResolver.openInputStream(uri)
        val outStream = File(app.filesDir, profilePictureFileName).outputStream()
        inStream?.also {
            it.copyTo(outStream)
        }
        loadProfilePicture()
        }

}

