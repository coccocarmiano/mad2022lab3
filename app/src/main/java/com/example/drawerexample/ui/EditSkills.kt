package com.example.drawerexample.ui

import android.os.Bundle
import android.util.Log
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class EditSkills : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var binding : EditSkillsFragmentBinding
    private lateinit var skillsAdapter : SkillsAdapter

    private val userSkillsSet = HashSet<String>()
    private val allSkillsSet = HashSet<String>()
    private val displaySkillsSet = HashMap<String, Boolean>()
    private var cnt = MutableLiveData(0)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditSkillsFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        updateUserSkills()
        updateAllSkills()

        cnt.observe(viewLifecycleOwner) {
            if ( it == 2 ){
                updateDisplaySkills()
                skillsAdapter = SkillsAdapter(displaySkillsSet)
                binding.skillsListRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context)
                    adapter = skillsAdapter
                }
            }
        }

        binding.saveSkillButton.setOnClickListener {
            displaySkillsSet.entries.filter { it.value }.map { it.key }.toList().run { userViewModel.updateSkills(this) }
            findNavController().popBackStack()
        }

        requireActivity()
            .onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        return root
    }

    private fun updateUserSkills() {
        userSkillsSet.clear()
        Firebase.auth.uid?.let { uid ->
            FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { doc ->
                    doc.get("skills")
                        ?.let { it as List<String> }
                        ?.onEach { userSkillsSet.add(it) }
                    cnt.value = cnt.value?.plus(1)
                }
        }
    }


    private fun updateAllSkills() {
        allSkillsSet.clear()
        FirebaseFirestore
            .getInstance()
            .collection("general")
            .get()
            .addOnSuccessListener { docs ->
                docs.forEach { doc ->
                    doc.get("skills")
                        ?.let { it as List<String> }
                        ?.onEach { allSkillsSet.add(it) }
                    cnt.value = cnt.value?.plus(1)
                }
            }
    }

    fun updateDisplaySkills() {
        displaySkillsSet.clear()
        allSkillsSet.forEach { skill ->
            displaySkillsSet[skill] = userSkillsSet.contains(skill)
        }

    }
}