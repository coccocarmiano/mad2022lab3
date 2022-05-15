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

    private var advIndex : Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)

        binding = ShowTimeSlotDetailsFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        advIndex = arguments?.getInt("adv_index")?.also { advIdx ->
            advertisementViewModel.liveAdvList.value?.get(advIdx)?.let {
                refreshUI(it)
            }
        }

        advertisementViewModel.liveAdvList.observe(viewLifecycleOwner) {
            advIndex?.let { advIdx ->
                advertisementViewModel.liveAdvList.value?.get(advIdx)?.also { refreshUI(it) }
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
