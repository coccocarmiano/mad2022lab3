package com.example.lab3

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()
    private val advertisementViewModel:AdvertisementViewModel by viewModels()
    val requestPhotoForProfileEdit = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestPhotoForProfileEdit) {
            when (resultCode) {
                RESULT_OK -> {
                    // If Bitmap
                    data?.extras?.get("data")?.let {
                        it as Bitmap
                    }?.run {
                        userViewModel.storeProfilePicture(this)
                    }

                    // If URI
                    data?.data?.let {
                        userViewModel.updateProfilePictureFromURI(it)
                    }
                }
                RESULT_CANCELED -> {
                    Snackbar
                        .make(findViewById(R.id.editProfileFragment), "Operation canceled", Snackbar.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    Snackbar
                        .make(findViewById(R.id.editProfileFragment), "An unknown error occurred", Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}