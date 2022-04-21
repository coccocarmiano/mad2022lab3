package com.example.lab3

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.edit_profile_fragment.view.*
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class UserViewModel(private val app: Application) : AndroidViewModel(app) {


    // How to retrieve resources without context?
    val fullname = MutableLiveData<String>()
    val username = MutableLiveData<String>()
    val location = MutableLiveData<String>()
    val mail = MutableLiveData<String>()
    val profilePictureBitmap = MutableLiveData<Bitmap>()
    val profilePictureFileName = "profilePicture.png"

    init {
        val jsonString = app.getSharedPreferences("user", Context.MODE_PRIVATE).getString("user", "{}") ?: "{}"
        val json = JSONObject(jsonString)

        json.apply {
            fullname.value = optString("fullname", app.getString(R.string.fullname_placeholder_text))
            username.value = optString("username", app.getString(R.string.username_placeholder_text))
            location.value = optString("location", app.getString(R.string.location_placeholder_text))
            mail.value = optString("mail", app.getString(R.string.email_placeholder_text))
            // Skills...
        }

        loadProfilePicture()
    }

    fun save() {
        JSONObject()
            .apply {
                put("fullname", fullname.value)
                put("username", username.value)
                put("location", location.value)
                put("mail", mail.value)
                // Skills..?
            }.toString()
            .run {
                 app
                .getSharedPreferences("user", Context.MODE_PRIVATE)
                .edit()
                .putString("user", this)
                .apply()
            }

        // TODO This has to be changed
        profilePictureBitmap.value?.let { storeProfilePicture(it) }
    }

    // TODO This has to be removed when methods below are implemented
    private fun storeProfilePicture(bmp : Bitmap) {
        bmp.compress(Bitmap.CompressFormat.PNG, 100, app.openFileOutput("profile.png", Context.MODE_PRIVATE))
    }

    private fun loadProfilePicture() : Bitmap {
        return File(profilePictureFileName)
            .run {
                when (exists()) {
                    true -> BitmapFactory.decodeFile(this.absolutePath)
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

    // This should be deleted...
    fun updateProfilePictureFromImage() {
        // TODO Implement, possibly not from Bitmap
    }

    // Not tested
    fun updateProfilePictureFromURI(uri : Uri) {
        val inFile = FileInputStream(uri.path)
        val outFile = FileOutputStream(profilePictureFileName)
        inFile.copyTo(outFile)
    }

}