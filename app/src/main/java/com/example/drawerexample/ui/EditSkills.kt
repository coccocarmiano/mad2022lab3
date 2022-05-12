package com.example.drawerexample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawerexample.adapter.SkillsAdapter
import com.example.drawerexample.databinding.EditSkillsFragmentBinding
import com.example.drawerexample.viewmodel.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore

class EditSkills : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var binding : EditSkillsFragmentBinding
    private var skillsList : MutableLiveData<List<String>> = MutableLiveData()
    private lateinit var skillsAdapter : SkillsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditSkillsFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        skillsList.observe(viewLifecycleOwner) {
            skillsAdapter = SkillsAdapter(it.toTypedArray(), userViewModel.liveSkills)
            binding.skillsListRecyclerView.apply {
                adapter = skillsAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }

        getSkillsList()

        binding.saveSkillButton.setOnClickListener {
            skillsList.value?.run {
                userViewModel.updateSkills(this)
            }
            findNavController().popBackStack()
        }

        requireActivity()
            .onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        return root
    }

    private fun getSkillsList() {
        FirebaseFirestore
            .getInstance()
            .collection("general")
            .document("skills")
            .get()
            .addOnSuccessListener {
                skillsList.value = try {
                    it.data?.get("skills") as List<String>
                } catch (e: ClassCastException) {
                    e.printStackTrace()
                    listOf()
                }
            }
            .addOnFailureListener {
                skillsList = MutableLiveData()
            }
    }
}