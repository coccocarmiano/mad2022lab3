package com.example.drawerexample

import androidx.lifecycle.MutableLiveData

class UserProfile {
    var fullname = ""
    var username = ""
    var location = ""
    var mail = ""
    var skills = listOf<String>()

    val profilePictureFilename = "profile_picture.png"

    constructor(userProfile : UserProfile) {
        fullname = userProfile.fullname
        username = userProfile.username
        location = userProfile.location
        mail = userProfile.mail
        skills = userProfile.skills
    }

    constructor() {}
}