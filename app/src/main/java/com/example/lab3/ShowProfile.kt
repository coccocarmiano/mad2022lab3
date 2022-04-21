package com.example.lab3

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.show_profile_fragment.view.*

class ShowProfile : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.show_profile_fragment, container, false)

        view.fullNameTV.text = userViewModel.fullname.value
        view.emailTV.text = userViewModel.mail.value
        view.locationTV.text = userViewModel.location.value
        view.usernameTV.text = userViewModel.username.value
        view.profileImageShowProfile.setImageBitmap(userViewModel.profilePictureBitmap.value)
        return view
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.optionsMenuEditProfile -> {
                findNavController().navigate(R.id.action_showProfile_to_editProfile)
            }
            else -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }
}