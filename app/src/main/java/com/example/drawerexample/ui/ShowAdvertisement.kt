package com.example.drawerexample.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.drawerexample.Advertisement
import com.example.drawerexample.R
import com.example.drawerexample.viewmodel.AdvertisementViewModel
import com.example.drawerexample.databinding.ShowTimeSlotDetailsFragmentBinding

class ShowAdvertisement : Fragment() {

    private val advertisementViewModel: AdvertisementViewModel by activityViewModels()

    private var _binding: ShowTimeSlotDetailsFragmentBinding? = null
    private val binding get() = _binding!!

    private var advIndex : Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        _binding = ShowTimeSlotDetailsFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        advIndex = arguments?.getInt("adv_index", -1)
        if (advIndex != null && advIndex != -1) {
            val adv = advertisementViewModel.liveAdvList.value?.get(advIndex!!)

            refreshUI(adv)
        }

        advertisementViewModel.liveAdvList.observe(viewLifecycleOwner) {
            if (advIndex != null && advIndex != -1) {
                val adv = advertisementViewModel.liveAdvList.value?.get(advIndex!!)

                refreshUI(adv)
            }
        }

        return root
    }

    private fun refreshUI(adv : Advertisement?) {
        adv.apply {
            binding.textTitle.setText(adv?.title)
            binding.textDescription.setText(adv?.description)
            binding.textDuration.setText(adv?.duration)
            binding.textLocation.setText(adv?.location)
            binding.textDate.setText(adv?.date)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_edit_only, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_edit -> {
                val bundle = Bundle()
                advIndex?.let { bundle.putInt("adv_index", it) }
                findNavController().navigate(R.id.action_nav_show_adv_to_nav_edit_adv, bundle)
            }
            else -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }
}
