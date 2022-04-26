package com.example.lab3

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.edit_time_slot_details_fragment.view.*
import java.util.*

class EditAdvertisement : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private val advertisementViewModel: AdvertisementViewModel by activityViewModels()
    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0
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
        getDateTimeCalendar()
        view.dateAdvertisementEditTV.setText(dateTimeToString(day,month,year,hour,minute) )



        view.editDateAdvertisement.setOnClickListener {
            getDateTimeCalendar()
            DatePickerDialog( this.requireContext(), this, year, month, day).show()
            //TODO questo aggiornamento non funziona bene
            view.dateAdvertisementEditTV.setText(dateTimeToString(savedDay,savedMonth,savedYear,savedHour,savedMinute) )
            }

        view.editAdvertisementSaveButton.setOnClickListener {
            advertisementViewModel.updateFromEditAdvertisement(view)
            findNavController().popBackStack()

        }


        return view

    }


    private fun dateTimeToString(day: Int, month: Int, year: Int, hour: Int, minute: Int): String{
        val dataString = "" + day + "/" + month + "/" + year + " at "+ hour + ":" + minute
        return dataString
    }
    private fun getDateTimeCalendar(){
        val cal = Calendar.getInstance()
        day= cal.get(Calendar.DAY_OF_MONTH)
        month= cal.get(Calendar.MONTH)
        year= cal.get(Calendar.YEAR)
        hour= cal.get(Calendar.HOUR)
        minute= cal.get(Calendar.MINUTE)
    }

    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, day: Int) {
        savedDay= day
        savedMonth= month
        savedYear = year
        getDateTimeCalendar()
        TimePickerDialog(this.requireContext(),this, hour, minute, true).show()
    }

    override fun onTimeSet(p0: TimePicker?, hour: Int, minute: Int) {
        savedHour = hour
        savedMinute = minute
    }
}