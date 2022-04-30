package com.example.drawerexample.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawerexample.adapter.SkillsAdapter
import com.example.drawerexample.databinding.EditSkillsFragmentBinding
import com.example.drawerexample.viewmodel.UserViewModel

class EditSkills : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()

    private var _binding: EditSkillsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = EditSkillsFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val skillsAdapter = SkillsAdapter(
            listOf(
                "Gardening", "App Development", "Cooking", "Programming", "Reading",
            ).toTypedArray(), userViewModel.liveSkills)
        val recyclerView = binding.skillsListRecyclerView
        recyclerView.run {
            adapter = skillsAdapter
            layoutManager = LinearLayoutManager(container?.context)
        }

        binding.saveSkillButton.setOnClickListener {
            activity?.onBackPressed()
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}