package com.example.drawerexample.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
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

class MyAdvListFragment : Fragment() {

    private val advViewModel: AdvertisementViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var binding : FragmentAdvListBinding

    private var filtersOpened: Boolean = false

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

        userViewModel.liveUser.observe(viewLifecycleOwner) {
            advViewModel.liveAdvList.value = advViewModel.liveAdvList.value
        }

        advViewModel.liveAdvList.observe(viewLifecycleOwner) { advList ->
            if (advList != null) {
                if (advList.isEmpty()) {
                    binding.noAdvTV.visibility = View.VISIBLE
                    binding.advListRv.visibility = View.GONE
                } else {
                    binding.noAdvTV.visibility = View.GONE
                    binding.advListRv.visibility = View.VISIBLE
                    val myAdvertisementList =
                        advList.filter { it.emailCreator == userViewModel.liveUser.value!!.mail }
                    advAdapter.data = myAdvertisementList.toMutableList()
                    advAdapter.notifyDataSetChanged()
                }
            }
        }

        binding.advAddFb.setOnClickListener {
            findNavController().navigate(R.id.action_nav_adv_myList_to_nav_edit_adv)
        }

        binding.filtersDropdownBtn.setOnClickListener {
            filtersOpened = !filtersOpened
            if (filtersOpened) {
                binding.filtersDropdownBtn.setImageResource(R.drawable.arrow_drop_up)
                binding.filtersBody.visibility = View.VISIBLE
            } else {
                binding.filtersDropdownBtn.setImageResource(R.drawable.arrow_drop_down)
                binding.filtersBody.visibility = View.GONE
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