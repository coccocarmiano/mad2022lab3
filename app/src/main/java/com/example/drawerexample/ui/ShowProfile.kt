package com.example.drawerexample.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.drawerexample.R
import com.example.drawerexample.databinding.ShowProfileFragmentBinding
import com.example.drawerexample.viewmodel.UserViewModel


class ShowProfile : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()

    private var _binding: ShowProfileFragmentBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        _binding = ShowProfileFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userViewModel.liveUser.observe(viewLifecycleOwner, Observer {
            binding.fullNameTV.text = it.fullname
            binding.emailTV.text = it.mail
            binding.locationTV.text = it.location
            binding.usernameTV.text = it.username })

        userViewModel.liveSkills.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty())
                binding.skillsTV.text = getString(R.string.no_skills)
            else
                binding.skillsTV.text = it.joinToString(", ")})

        userViewModel.livePicture.observe(viewLifecycleOwner, Observer {
            binding.profileImageShowProfile.setImageBitmap(it)
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_edit_only, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_edit -> {
                findNavController().navigate(R.id.action_nav_show_profile_to_nav_edit_profile)
            }
            else -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }

}