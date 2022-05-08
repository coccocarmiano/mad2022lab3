package com.example.drawerexample.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawerexample.R
import com.example.drawerexample.adapter.AdvertisementsAdapter
import com.example.drawerexample.viewmodel.AdvertisementViewModel
import com.example.drawerexample.databinding.FragmentAdvListBinding

class AdvListFragment : Fragment() {

    private val advViewModel: AdvertisementViewModel by activityViewModels()

    private var _binding: FragmentAdvListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAdvListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val advAdapter = AdvertisementsAdapter(this)

        val recyclerView = binding.advListRv
        recyclerView.run {
            adapter = advAdapter
            layoutManager = LinearLayoutManager(container?.context)
        }

        advViewModel.liveAdvList.observe(viewLifecycleOwner) {
            advAdapter.data = it
            advAdapter.notifyDataSetChanged()
        }

        binding.advAddFb.setOnClickListener {
            findNavController().navigate(R.id.action_nav_adv_list_to_nav_edit_adv)
        }

        binding.noAdvTV.apply {
            visibility = if (advViewModel.liveAdvList.value.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}