package com.example.drawerexample.ui

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.drawerexample.MainActivity
import com.example.drawerexample.R
import com.example.drawerexample.databinding.EditProfileFragmentBinding
import com.example.drawerexample.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar

class EditProfile : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var binding : EditProfileFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditProfileFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setHasOptionsMenu(true)
        startListeningForChanges()

        binding.editProfileManageSkillsButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_edit_profile_to_nav_edit_skills)
        }

        binding.editProfileSaveButton.setOnClickListener {
            when (allFieldsAreValid()) {
                true -> {
                    updateViewModel()
                    findNavController().popBackStack()
                }
                else -> Snackbar.make(root, "Please fill all the fields", Snackbar.LENGTH_LONG)
                    .show()
            }
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
                    findNavController().popBackStack()
                }
            })

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {
                findNavController().popBackStack()
            }

        return root
    }

    private fun allFieldsAreValid(): Boolean {
        return !(binding.textInputEditFullName.text.toString().isEmpty() ||
                binding.textInputEditMail.text.toString().isEmpty() ||
                binding.textInputEditUserLocation.text.toString().isEmpty() ||
                binding.textInputEditUserName.text.toString().isEmpty())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        findNavController().popBackStack()
        return true
    }

    private fun updateViewModel() {
        userViewModel.apply {
            fullname.value = binding.textInputEditFullName.text.toString()
            username.value = binding.textInputEditUserName.text.toString()
            email.value = binding.textInputEditMail.text.toString()
            location.value = binding.textInputEditUserLocation.text.toString()
            userViewModel.applyChangesToFirebase()
        }
    }

    private fun startListeningForChanges() {
        userViewModel.fullname.observe(viewLifecycleOwner) {
            binding.textInputEditFullName.setText(it)
        }

        userViewModel.username.observe(viewLifecycleOwner) {
            binding.textInputEditUserName.setText(it)
        }

        userViewModel.email.observe(viewLifecycleOwner) {
            binding.textInputEditMail.setText(it)
        }

        userViewModel.location.observe(viewLifecycleOwner) {
            binding.textInputEditUserLocation.setText(it)
        }

        userViewModel.propic.observe(viewLifecycleOwner) {
            binding.profileImageEditProfile.setImageBitmap(it)
        }
    }

}