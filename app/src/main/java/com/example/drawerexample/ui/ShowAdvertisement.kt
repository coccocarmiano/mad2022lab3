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
    private lateinit var binding : ShowTimeSlotDetailsFragmentBinding
    private var allowEdit = false

    private var advID : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(allowEdit)
        arguments?.getBoolean("allowEdit")?.also { allowEdit = it }
        setHasOptionsMenu(allowEdit)

        binding = ShowTimeSlotDetailsFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        advID = arguments?.getString("advertisementID")?.also { advID ->
            advertisementViewModel.liveAdvList.value?.find { it.id == advID }?.let {
                refreshUI(it)
            }
        }

        advertisementViewModel.liveAdvList.observe(viewLifecycleOwner) {
            advID?.let { advID ->
                advertisementViewModel.liveAdvList.value?.find { it.id == advID }?.let { refreshUI(it) }
            }
        }

        return root
    }

    private fun refreshUI(adv : Advertisement?) {
        adv?.apply {
            binding.textTitle.setText(title)
            binding.textDescription.setText(description)
            binding.textDuration.setText(duration)
            binding.textLocation.setText(location)
            binding.textDate.setText(date)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_edit_only, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_edit -> {
                val bundle = Bundle().apply {
                    putString("advertisementID", advID)
                    putBoolean("allowEdit", allowEdit)
                }
                findNavController().navigate(R.id.action_nav_show_adv_to_nav_edit_adv, bundle)
            }
            else -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }
}
