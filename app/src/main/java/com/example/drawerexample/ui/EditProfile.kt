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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.drawerexample.MainActivity
import com.example.drawerexample.R
import com.example.drawerexample.UserProfile
import com.example.drawerexample.databinding.EditProfileFragmentBinding
import com.example.drawerexample.viewmodel.UserViewModel

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

        binding.fullNameET.setText(user.fullname)
        binding.emailET.setText(user.mail)
        binding.locationET.setText(user.location)
        binding.usernameET.setText(user.username)
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

    fun saveAndExit() {
        val user = UserProfile()

        user.fullname = binding.fullNameET.text.toString()
        user.mail = binding.emailET.text.toString()
        user.location = binding.locationET.text.toString()
        user.username = binding.usernameET.text.toString()

        userViewModel.liveUser.value = user
        findNavController().popBackStack()
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