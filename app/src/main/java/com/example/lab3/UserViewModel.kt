package com.example.lab3

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {

    private fun getString(resId: Int): String {
        return Resources.getSystem().getString(resId)
    }

    // How to retrieve resources without context?
    val fullname = MutableLiveData("Full Name")
    val username = MutableLiveData("Username")
    val location = MutableLiveData("Location")
    val mail = MutableLiveData("eMail")

        // Add shared preferences retrieval here
}