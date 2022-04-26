package com.example.lab3

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.show_time_slot_details_fragment.view.*

class ShowAdvertisement : Fragment() {

    private val advertisementViewModel: AdvertisementViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val view = inflater.inflate(R.layout.show_time_slot_details_fragment,container, false)
        view.titleAdvertisementTV.text = advertisementViewModel.title.value
        view.descriptionAdvertisementTV.text= advertisementViewModel.description.value
        view.dateAdvertisementTV.text= advertisementViewModel.date.value
        view.durationAdvertisementTV.text= advertisementViewModel.duration.value
        view.locationAdvertisementTV.text=advertisementViewModel.location.value


        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.advertisement_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.advertisementMenuEditAdvertisement -> {
                findNavController().navigate(R.id.action_showAdvertisement_to_editAdvertisement)
            }
            else -> {

            }
        }
        return super.onOptionsItemSelected(item)
    }
}
