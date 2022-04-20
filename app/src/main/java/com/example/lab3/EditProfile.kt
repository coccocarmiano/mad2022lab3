package com.example.lab3

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}