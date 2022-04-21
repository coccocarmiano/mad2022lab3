package com.example.lab3

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    val userViewModel: UserViewModel by viewModels()
    val requestPhotoForProfileEdit = 1;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestPhotoForProfileEdit) {
            val result = data?.extras?.get("data")
            when (resultCode) {
                RESULT_OK -> {
                    // TODO Check if there is a cleaner way of doing so
                    // If Bitmap
                    data?.extras?.get("data")?.let {
                        // Bitmap is kinda ugly to use, refer to
                        // https://developer.android.com/training/camera/photobasics
                        // create XMLs and to the saving via updateProfilePictureFromURI()
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