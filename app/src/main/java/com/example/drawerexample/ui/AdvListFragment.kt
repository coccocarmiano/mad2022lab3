package com.example.drawerexample.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawerexample.Advertisement
import com.example.drawerexample.R
import com.example.drawerexample.adapter.AdvertisementsAdapter
import com.example.drawerexample.viewmodel.AdvertisementViewModel
import com.example.drawerexample.databinding.FragmentAdvListBinding
import com.example.drawerexample.viewmodel.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects

class AdvListFragment : Fragment() {

    private val advViewModel: AdvertisementViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var binding : FragmentAdvListBinding
    private lateinit var skillsSpinnerAdapter: ArrayAdapter<String>
    private val skillsMap = HashMap<String, Boolean>()

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

        //advViewModel.liveAdvList.observe(viewLifecycleOwner) {
        //    advAdapter.data = it
        //    advAdapter.notifyDataSetChanged()
        //}


        skillsSpinnerAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, mutableListOf())
        skillsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.skillSpinner.adapter = skillsSpinnerAdapter
        skillsSpinnerAdapter.clear()
        skillsSpinnerAdapter.add("All")
        skillsSpinnerAdapter.addAll(skillsMap.keys)
        binding.skillSpinner.setSelection(0, true)
        skillsMap.clear()
        FirebaseFirestore
            .getInstance()
            .collection("general")
            .get()
            .addOnSuccessListener { docs ->
                docs.forEach { doc ->
                    doc.get("skills")
                        ?.let { it as List<String> }
                        ?.onEach { skillsMap[it] = userViewModel.liveUser.value?.skills?.contains(it) ?: true }
                        ?.also {
                            skillsSpinnerAdapter.addAll(it)
                            skillsSpinnerAdapter.notifyDataSetChanged()
                        }
                }
            }



        advViewModel.liveAdvList.observe(viewLifecycleOwner) {
            val myAdvertisementList : List<Advertisement>
            if(binding.skillSpinner.selectedItem=="All")
                myAdvertisementList =it.filter { true }
            else
                myAdvertisementList =it.filter { it.skill==binding.skillSpinner.selectedItem }
            advAdapter.data = myAdvertisementList.toMutableList()
            advAdapter.notifyDataSetChanged()

        }

        binding.advAddFb.setOnClickListener {
            findNavController().navigate(R.id.action_nav_adv_list_to_nav_edit_adv)
        }

        binding.noAdvTV.apply {
            visibility = if (advViewModel.liveAdvList.value.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        return root
    } }

