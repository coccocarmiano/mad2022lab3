package com.example.drawerexample.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.drawerexample.R
import com.example.drawerexample.UserProfile
import org.json.JSONObject
import java.io.File


class UserViewModel(private val app: Application) : AndroidViewModel(app) {

    var observer : Observer<UserProfile>
    val liveUser = MutableLiveData<UserProfile>()
    val livePicture = MutableLiveData<Bitmap>()
    val liveSkills = MutableLiveData<MutableList<String>>()

    init {
        val jsonString = app.getSharedPreferences("user", Context.MODE_PRIVATE).getString("user", "{}") ?: "{}"
        val json = JSONObject(jsonString)

        val user = UserProfile()

        json.apply {
            user.fullname = optString("fullname", app.getString(R.string.fullname_placeholder_text))
            user.username = optString("username", app.getString(R.string.username_placeholder_text))
            user.location = optString("location", app.getString(R.string.location_placeholder_text))
            user.mail = optString("mail", app.getString(R.string.email_placeholder_text))
            liveSkills.value = optString("skills", "").let {
                when (it) {
                    "" -> mutableListOf()
                    else -> it.split(",").toMutableList()
                }
            }
        }
        livePicture.value = loadProfilePicture(user.profilePictureFilename);
        liveUser.value = user

        observer = Observer { save(it) }
        liveUser.observeForever(observer)
    }

    private fun save(user : UserProfile) {
        JSONObject()
            .apply {
                put("fullname", user.fullname)
                put("username", user.username)
                put("location", user.location)
                put("mail", user.mail)
                put("skills", liveSkills.value?.joinToString(",") ?: "")
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

    override fun onCleared() {
        liveUser.removeObserver(observer)
        super.onCleared()
    }
}

