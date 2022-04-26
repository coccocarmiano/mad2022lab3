package com.example.lab3

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.edit_time_slot_details_fragment.view.*
import java.util.*

class EditAdvertisement : Fragment() {

    private val advertisementViewModel: AdvertisementViewModel by activityViewModels()

    var savedDay = 0
    var savedMonth = 0
    var savedYear = 0
    var savedHour = 0
    var savedMinute = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.edit_time_slot_details_fragment, container, false)

        view.titleAdvertisementET.setText(advertisementViewModel.title.value)
        view.descriptionAdvertisementET.setText(advertisementViewModel.description.value)
        view.locationAdvertisementET.setText(advertisementViewModel.location.value)
        view.durationAdvertisementET.setText(advertisementViewModel.duration.value)
        view.dateAdvertisementEditTV.setText(advertisementViewModel.date.value )

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    advertisementViewModel.updateFromEditAdvertisement(view)
                    findNavController().popBackStack()
                }
            })

        fun refresh(){
            view.dateAdvertisementEditTV.setText(dateTimeToString(savedDay,savedMonth,savedYear,savedHour,savedMinute))

        }

        val timePicker = TimePickerDialog.OnTimeSetListener{view,hour,minute->
            savedHour=hour
            savedMinute=minute
            refresh()

        }
        val datePicker=DatePickerDialog.OnDateSetListener{view,year,month,day->
            savedDay=day
            savedYear=year
            savedMonth=month
            println("ciao"+savedDay + " "+ savedYear+ " "+ savedMonth )
            TimePickerDialog(this.requireContext(),timePicker,savedHour,savedMinute,true).show()
        }

        view.editDateAdvertisement.setOnClickListener {
            getDateTimeCalendar()
            DatePickerDialog(this.requireContext(),datePicker,savedYear,savedMonth,savedDay).show()
        }

        view.editAdvertisementSaveButton.setOnClickListener {
            advertisementViewModel.updateFromEditAdvertisement(view)
            findNavController().popBackStack()
        }

        return view

    }



    private fun dateTimeToString(day: Int, month: Int, year: Int,hour:Int, minute:Int): String{
        //soluzione veloce al problema del minuto <10 essendo intero
        var dataString= ""
        if(minute<10)
            dataString = "" + day + "/" + month + "/" + year + " at " + hour + ":0" + minute
        else
            dataString = "" + day + "/" + month + "/" + year + " at " + hour + ":" + minute
        return dataString
    }
    private fun getDateTimeCalendar(){

        val cal = Calendar.getInstance()
        savedDay= cal.get(Calendar.DAY_OF_MONTH)
        savedMonth= cal.get(Calendar.MONTH)
        savedYear= cal.get(Calendar.YEAR)
        savedHour= cal.get(Calendar.HOUR)
        savedMinute= cal.get(Calendar.MINUTE)
    }



}