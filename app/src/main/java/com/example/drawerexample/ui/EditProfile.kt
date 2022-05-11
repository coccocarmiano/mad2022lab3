package com.example.drawerexample.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.drawerexample.MainActivity
import com.example.drawerexample.R
import com.example.drawerexample.UserProfile
import com.example.drawerexample.databinding.EditProfileFragmentBinding
import com.example.drawerexample.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar

class EditProfile : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()

    private var _binding: EditProfileFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EditProfileFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setHasOptionsMenu(true)

        val user = userViewModel.liveUser.value!!

        binding.textInputEditFullName.setText(user.fullname)
        binding.textInputEditMail.setText(user.mail)
        binding.textInputEditUserLocation.setText(user.location)
        binding.textInputEditUserName.setText(user.username)
        binding.profileImageEditProfile.setImageBitmap(userViewModel.livePicture.value)

        userViewModel.livePicture.observe(viewLifecycleOwner) {
            binding.profileImageEditProfile.setImageBitmap(it)
        }

        binding.editProfileManageSkillsButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_edit_profile_to_nav_edit_skills)
        }

        binding.editProfileSaveButton.setOnClickListener {
            saveAndExit()
        }

        binding.editProfileImageButton.setOnClickListener {
            val galleryIntent = Intent().setType("image/*").setAction(Intent.ACTION_GET_CONTENT)
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val chooserIntent = Intent.createChooser(galleryIntent, "Upload an Image").run { putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent)) }
            @Suppress("DEPRECATION")
            activity?.startActivityForResult(chooserIntent, (activity as MainActivity).requestPhotoForProfileEdit)
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    saveAndExit()
                }
            })

        return root
    }

    private fun checkSave(): Boolean {
        var check = true
        if( binding.textInputEditFullName.text.toString().isEmpty() ||
            binding.textInputEditMail.text.toString().isEmpty() ||
            binding.textInputEditUserLocation.text.toString().isEmpty() ||
            binding.textInputEditUserName.text.toString().isEmpty()
        )
            check = false
        return check
    }

    fun saveAndExit() {
        if(!checkSave()){
            Snackbar
                .make(binding.mainScrollView as View, "Please complete the form", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.RED)
                .show()
        }else {
            val user = UserProfile()
            user.fullname = binding.textInputEditFullName.text.toString()
            user.mail = binding.textInputEditMail.text.toString()
            user.location = binding.textInputEditUserLocation.text.toString()
            user.username = binding.textInputEditUserName.text.toString()
            userViewModel.liveUser.value = user

            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        saveAndExit()
        return true
    }

}