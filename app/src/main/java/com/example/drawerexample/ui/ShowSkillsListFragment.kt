package com.example.drawerexample.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawerexample.adapter.ShowSkillListAdapter
import com.example.drawerexample.databinding.FragmentShowSkillsListBinding
import com.example.drawerexample.viewmodel.AdvertisementViewModel

class ShowSkillsListFragment : Fragment() {

    private lateinit var binding : FragmentShowSkillsListBinding
    private val advViewModel: AdvertisementViewModel by viewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShowSkillsListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val skillsAdapter = ShowSkillListAdapter(this)

        val recyclerView = binding.offeredSkillsRv
        recyclerView.run {
            adapter = skillsAdapter
            layoutManager = LinearLayoutManager(container?.context)
        }

        advViewModel.liveAdvList.observe(viewLifecycleOwner) { advList ->
            when {
                advList.isNullOrEmpty() -> {
                    binding.noSkills.visibility = View.VISIBLE
                    binding.offeredSkillsRv.visibility = View.GONE
                }
                else -> {
                    binding.noSkills.visibility = View.GONE
                    binding.offeredSkillsRv.visibility = View.VISIBLE
                    skillsAdapter.data = advList.map { it.skill }.distinct()
                    skillsAdapter.notifyDataSetChanged()
                }
            }
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finishAndRemoveTask()
                }
            })

        return root
    }
}