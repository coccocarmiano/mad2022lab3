package com.example.drawerexample.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
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
import com.example.drawerexample.R
import com.example.drawerexample.databinding.EditTimeSlotDetailsFragmentBinding
import com.example.drawerexample.viewmodel.AdvertisementViewModel
import com.google.android.material.snackbar.Snackbar
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
                binding.dateAdvertisementEditET.setText(adv?.date)
            }
        }


        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    saveAndExit()
                }
            })

        binding.editDateAdvertisement.setOnClickListener {
            getDateTimeCalendar()
            DatePickerDialog(this.requireContext(),this,savedYear,savedMonth,savedDay).show()
        }

        binding.editAdvertisementSaveButton.setOnClickListener {
            saveAndExit()
        }

        return root
    }

    fun checkSave(): Boolean {
        var check = true
        if( binding.titleAdvertisementET.text.toString().length==0||
            binding.descriptionAdvertisementET.text.toString().length==0||
            binding.locationAdvertisementET.text.toString().length==0||
            binding.durationAdvertisementET.text.toString().length==0||
            binding.dateAdvertisementEditET.text.toString().length==0    )
        check = false
        return check
    }

    fun saveAndExit() {
        if(!checkSave())
            Snackbar
                .make(binding.root, "Please complete the form", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.RED)
                .show()
        else {
            val adv = Advertisement()
            adv.title = binding.titleAdvertisementET.text.toString()
            adv.description = binding.descriptionAdvertisementET.text.toString()
            adv.location = binding.locationAdvertisementET.text.toString()
            adv.duration = binding.durationAdvertisementET.text.toString()
            adv.date = binding.dateAdvertisementEditET.text.toString()

            if (advIndex == null || advIndex == -1) {
                advertisementViewModel.liveAdvList.value?.add(adv)
            } else {
                advertisementViewModel.liveAdvList.value?.set(advIndex!!, adv)
            }
            advertisementViewModel.liveAdvList.value = advertisementViewModel.liveAdvList.value

            findNavController().popBackStack()
        }
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

        binding.dateAdvertisementEditET.setText(dateTimeToString(savedDay,savedMonth,savedYear,savedHour,savedMinute))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        saveAndExit()
        return true
    }

}