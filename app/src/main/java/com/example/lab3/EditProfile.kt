package com.example.lab3

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.edit_profile_fragment.view.*

class EditProfile : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.edit_profile_fragment, container, false)

        view.fullNameET.setText(userViewModel.fullname.value)
        view.emailET.setText(userViewModel.mail.value)
        view.locationET.setText(userViewModel.location.value)
        view.usernameET.setText(userViewModel.username.value)


        view.editProfileManageSkillsButton.setOnClickListener {
            findNavController().navigate(R.id.action_editProfile_to_editSkills)
        }

        view.editProfileSaveButton.setOnClickListener {
            userViewModel.fullname.value = view.fullNameET.text.toString()
            userViewModel.mail.value = view.emailET.text.toString()
            userViewModel.location.value = view.locationET.text.toString()
            userViewModel.username.value = view.usernameET.text.toString()
            findNavController().popBackStack()
        }

        view.editProfileImageButton.setOnClickListener {
            val galleryIntent = Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT)
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val chooserIntent = Intent.createChooser(galleryIntent, "Upload an Image").run { putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent)) }

            activity?.startActivityForResult(chooserIntent, (activity as MainActivity).requestPhotoForProfileEdit)
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}