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

    private val skillsMap = HashMap<String, Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditSkillsFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        skillsAdapter = SkillsAdapter(skillsMap)
        binding.skillsListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = skillsAdapter
        }

        refreshSkillsStatus()
        userViewModel.liveUser.observe(viewLifecycleOwner) { refreshSkillsStatus() }

        binding.saveSkillButton.setOnClickListener {
            skillsAdapter.getData().entries.filter { it.value }.map { it.key }.toList().run { userViewModel.updateSkills(this) }
            findNavController().popBackStack()
        }

        requireActivity()
            .onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }

        return root
    }

    private fun refreshSkillsStatus() {
        skillsMap.clear()
        FirebaseFirestore
            .getInstance()
            .collection("general")
            .get()
            .addOnSuccessListener { docs ->
                docs.forEach { doc ->
                    doc.get("skills")
                        ?.let { it as List<String> }
                        ?.onEach { skillsMap[it] = userViewModel.liveUser.value?.skills?.contains(it) ?: false }
                        ?.also {
                            skillsAdapter.setData(skillsMap)
                            skillsAdapter.notifyDataSetChanged()
                        }
                }
            }
    }
}