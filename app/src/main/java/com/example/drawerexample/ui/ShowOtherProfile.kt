package com.example.drawerexample.ui

import android.os.Bundle
import android.view.*
import androidx.core.view.drawToBitmap
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.drawerexample.R
import com.example.drawerexample.databinding.ShowProfileFragmentBinding
import com.example.drawerexample.viewmodel.AdvertisementViewModel
import com.example.drawerexample.viewmodel.UserViewModel

class ShowOtherProfile : Fragment() {

    //TODO mostrare le informazione dell'user che ha creato l'adv cliccato

    private val advViewModel by viewModels<AdvertisementViewModel>()
    private lateinit var binding : ShowProfileFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = ShowProfileFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        advViewModel.creatorToShow.observe(viewLifecycleOwner){
            binding.fullNameTV.text = it.fullname
            binding.emailTV.text = it.mail
            binding.locationTV.text = it.location
            binding.usernameTV.text = it.username

            if (it.skills.isEmpty())
                binding.skillsTV.text = getString(R.string.no_skills)
            else
                binding.skillsTV.text = it.skills.joinToString(", ")
        }
        //TODO FOTO PROFILO
        /*
        userViewModel.livePicture.observe(requireActivity()) {
            binding.profileImageShowProfile.setImageBitmap(it)
        }
        */
        setHasOptionsMenu(false)
        return root
    }

    /*
    override fun onStart() {
        super.onStart()
        userViewModel.setUserPhoto()
    }

     */

    //TODO qua tasto per scrivere la messaggistica del prossimo lab
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_edit_only, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_edit -> {
                var b = Bundle()
                b.putBoolean("showCaller", true)
                findNavController().navigate(R.id.action_nav_show_profile_to_nav_edit_profile, b)
            }
            else -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

}