package com.example.drawerexample.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.drawerexample.Advertisement
import com.example.drawerexample.databinding.EditTimeSlotDetailsFragmentBinding
import com.example.drawerexample.viewmodel.AdvertisementViewModel
import java.text.SimpleDateFormat
import java.util.*

class EditAdvertisement : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private val advertisementViewModel: AdvertisementViewModel by activityViewModels()

    private var _binding: EditTimeSlotDetailsFragmentBinding? = null
    private val binding get() = _binding!!

    private var advIndex : Int? = null

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedHour = 0
    var savedMinute = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = EditTimeSlotDetailsFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setHasOptionsMenu(true)

        advIndex = arguments?.getInt("adv_index", -1)
        if (advIndex != null && advIndex != -1) {
            val adv = advertisementViewModel.liveAdvList.value?.get(advIndex!!)

            adv.apply {
                binding.titleAdvertisementET.setText(adv?.title)
                binding.descriptionAdvertisementET.setText(adv?.description)
                binding.locationAdvertisementET.setText(adv?.location)
                binding.durationAdvertisementET.setText(adv?.duration)
                binding.dateAdvertisementEditTV.text = adv?.date
            }
        }


        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    save()
                }
            })

        binding.editDateAdvertisement.setOnClickListener {
            getDateTimeCalendar()
            DatePickerDialog(this.requireContext(),this,savedYear,savedMonth,savedDay).show()
        }

        binding.editAdvertisementSaveButton.setOnClickListener {
            save()
        }

        return root
    }

    fun save() {
        val adv = Advertisement()
        adv.title = binding.titleAdvertisementET.text.toString()
        adv.description = binding.descriptionAdvertisementET.text.toString()
        adv.location = binding.locationAdvertisementET.text.toString()
        adv.duration = binding.durationAdvertisementET.text.toString()
        adv.date = binding.dateAdvertisementEditTV.text.toString()

        if (advIndex == null || advIndex == -1) {
            advertisementViewModel.liveAdvList.value?.add(adv)
        } else {
            advertisementViewModel.liveAdvList.value?.set(advIndex!!, adv)
        }
        advertisementViewModel.liveAdvList.value = advertisementViewModel.liveAdvList.value

        findNavController().popBackStack()
    }

    private fun dateTimeToString(day: Int, month: Int, year: Int,hour:Int, minute:Int): String{
        val c = Calendar.getInstance()

        c.set(year, month, day, hour, minute)

        val simpleDateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm")

        return simpleDateTimeFormat.format(c.time)
    }

    private fun getDateTimeCalendar(){
        val cal = Calendar.getInstance()
        savedDay= cal.get(Calendar.DAY_OF_MONTH)
        savedMonth= cal.get(Calendar.MONTH)
        savedYear= cal.get(Calendar.YEAR)
        savedHour= cal.get(Calendar.HOUR)
        savedMinute= cal.get(Calendar.MINUTE)
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        savedYear=p1
        savedMonth=p2
        savedDay=p3

        TimePickerDialog(this.requireContext(),this,savedHour,savedMinute,true).show()
    }

    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        savedHour=p1
        savedMinute=p2

        binding.dateAdvertisementEditTV.text = dateTimeToString(savedDay,savedMonth,savedYear,savedHour,savedMinute)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        save()
        return true
    }

}