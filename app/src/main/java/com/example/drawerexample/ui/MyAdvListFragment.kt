package com.example.drawerexample.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawerexample.Advertisement
import com.example.drawerexample.R
import com.example.drawerexample.adapter.AdvertisementsAdapter
import com.example.drawerexample.viewmodel.AdvertisementViewModel
import com.example.drawerexample.databinding.FragmentAdvListBinding
import com.example.drawerexample.viewmodel.UserViewModel

class MyAdvListFragment : Fragment() {

    private val advViewModel: AdvertisementViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var binding : FragmentAdvListBinding

    private lateinit var skillsSpinnerAdapter: ArrayAdapter<String>




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAdvListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val advAdapter = AdvertisementsAdapter(this)

        val recyclerView = binding.advListRv
        recyclerView.run {
            adapter = advAdapter
            layoutManager = LinearLayoutManager(container?.context)
        }

        skillsSpinnerAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, mutableListOf())
        skillsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.skillSpinner.adapter = skillsSpinnerAdapter

        userViewModel.liveUser.observe(viewLifecycleOwner) {
            skillsSpinnerAdapter.clear()
            skillsSpinnerAdapter.add("All")
            skillsSpinnerAdapter.addAll(it.skills)
            binding.skillSpinner.setSelection(0, true)
        }


        advViewModel.liveAdvList.observe(viewLifecycleOwner) {
            val myAdvertisementList : List<Advertisement>
            if(binding.skillSpinner.selectedItem=="All")
                myAdvertisementList =it.filter { it.emailCreator == userViewModel.liveUser.value!!.mail  }
            else
                myAdvertisementList =it.filter { it.emailCreator == userViewModel.liveUser.value!!.mail && it.skill==binding.skillSpinner.selectedItem }
            advAdapter.data = myAdvertisementList.toMutableList()
            advAdapter.notifyDataSetChanged()

        }

        binding.advAddFb.setOnClickListener {
            findNavController().navigate(R.id.action_nav_adv_myList_to_nav_edit_adv)
        }

        binding.noAdvTV.apply {
            visibility = if (advViewModel.liveAdvList.value.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        return root
    }
}