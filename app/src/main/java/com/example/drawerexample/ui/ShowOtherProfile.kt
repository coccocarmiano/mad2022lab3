package com.example.drawerexample.ui

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.drawerexample.R
import com.example.drawerexample.UserProfile
import com.example.drawerexample.databinding.ShowProfileFragmentBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.net.URL


class ShowOtherProfile : Fragment() {
    private val user = MutableLiveData(UserProfile())
    private val userID : MutableLiveData<String> = MutableLiveData()
    private lateinit var binding : ShowProfileFragmentBinding
    private lateinit var advID : String
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
        advID = arguments?.get("adv_ID") as String
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = ShowProfileFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        db.document("advertisements/$advID").get().addOnSuccessListener {
            it.get("emailCreator").let { it as String }.also { email ->
                db.collection("users").whereEqualTo("email", email).limit(1)
                    .addSnapshotListener { res, err ->
                        res?.let { it.documents[0] }?.also { doc ->
                            val username = doc.get("username") as String
                            val fullname = doc.get("fullName") as String
                            val location = doc.get("location") as String
                            val skills = doc.get("skills") as? List<String> ?: emptyList()
                            val id = doc.id
                            val mail = doc.get("email") as String

                            val newUser = UserProfile()
                            newUser.username = username
                            newUser.fullname = fullname
                            newUser.location = location
                            newUser.skills = skills
                            newUser.mail = mail
                            user.value = newUser
                            userID.value = id
                        }
                    }
            }
        }

        user.observe(viewLifecycleOwner) {
            binding.emailTV.text = it.mail
            binding.usernameTV.text = it.username
            binding.fullNameTV.text = it.fullname
            binding.locationTV.text = it.location
            binding.skillsTV.text = it.skills.joinToString(", ")
        }

        userID.observe(viewLifecycleOwner) {
            setPFP(it)
        }

        setHasOptionsMenu(false)
        return root
    }


    //TODO qua tasto per scrivere la messaggistica del prossimo lab
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_edit_only, menu)
    }

    private fun setPFP(uid : String) {
        Firebase.storage.reference
            .child("users_profile_pictures")
            .child(uid)
            .downloadUrl
            .addOnSuccessListener { photoURI ->
                GlobalScope.async {
                    URL(photoURI.toString()).openStream().use {
                        val bmp = BitmapFactory.decodeStream(it)
                        binding.profileImageShowProfile.setImageBitmap(bmp)
                    }
                }
            }.addOnFailureListener {
                Log.d("Error", ":(")
            }
    }
}