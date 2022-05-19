package com.example.drawerexample.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawerexample.Advertisement
import com.example.drawerexample.R
import com.example.drawerexample.adapter.AdvertisementsAdapter
import com.example.drawerexample.adapter.AdvertisementsAdapterNoEdit
import com.example.drawerexample.adapter.SkillsAdapter
import com.example.drawerexample.viewmodel.AdvertisementViewModel
import com.example.drawerexample.databinding.FragmentAdvListBinding
import com.example.drawerexample.viewmodel.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore


class AdvListFragment : Fragment() {

    private val advViewModel: AdvertisementViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var binding : FragmentAdvListBinding
    private lateinit var skillsSpinnerAdapter: ArrayAdapter<String>
    private val skillsMap = HashMap<String, Boolean>()
    private var skillSelected : String = "All"
    private var initialPosition : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = FragmentAdvListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val advAdapter = AdvertisementsAdapterNoEdit(this)

        val recyclerView = binding.advListRv
        recyclerView.run {
            adapter = advAdapter
            layoutManager = LinearLayoutManager(container?.context)
        }

        advViewModel.liveAdvList.observe(viewLifecycleOwner) {
            advAdapter.data = it
            advAdapter.notifyDataSetChanged()
        }

        skillsSpinnerAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, mutableListOf())
        skillsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.skillSpinner.adapter = skillsSpinnerAdapter
        skillsSpinnerAdapter.clear()
        skillsSpinnerAdapter.add("All")
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
                            if(skillSelected!="All")
                            initialPosition = it.indexOf(skillSelected)
                        }
                }
            }


        binding.skillSpinner.setSelection(initialPosition, true)


        binding.skillSpinner.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                skillSelected= binding.skillSpinner.selectedItem as String
                initialPosition=position
                advAdapter.notifyDataSetChanged()
                advViewModel.liveAdvList.observe(viewLifecycleOwner) {
                    val myAdvertisementList : List<Advertisement>
                    if(binding.skillSpinner.selectedItem=="All")
                        myAdvertisementList =it
                    else
                        myAdvertisementList =it.filter { it.skill==binding.skillSpinner.selectedItem }
                    advAdapter.data = myAdvertisementList.toMutableList()
                    advAdapter.notifyDataSetChanged()

                }

            }
        }


        binding.advAddFb.setOnClickListener {
            findNavController().navigate(R.id.action_nav_adv_list_to_nav_edit_adv)
        }

        binding.noAdvTV.apply {
            visibility = if (advViewModel.liveAdvList.value.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        return root
    }
}


