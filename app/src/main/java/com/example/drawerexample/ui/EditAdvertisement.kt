package com.example.drawerexample.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import com.example.drawerexample.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class EditAdvertisement : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private val advertisementViewModel: AdvertisementViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    private var _binding: EditTimeSlotDetailsFragmentBinding? = null
    private val binding get() = _binding!!

    private var advID : String? = null

    private var initialAdvSkill : String? = null

    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var savedHour = 0
    private var savedMinute = 0

    private lateinit var skillsSpinnerAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = EditTimeSlotDetailsFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setHasOptionsMenu(true)

        advID = arguments?.getString("adv_ID", "")

        if (advID != null && advID != "") {
            val adv = advertisementViewModel.liveAdvList.value?.find { it.id == advID }

            adv.apply {
                binding.textInputEditTitle.setText(adv?.title)
                binding.textInputEditDescription.setText(adv?.description)
                binding.textInputEditLocation.setText(adv?.location)
                binding.textInputEditDuration.setText(adv?.duration)
                binding.textInputEditDate.setText(adv?.date)

                initialAdvSkill = adv?.skill
            }
        }

        skillsSpinnerAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, mutableListOf())
        skillsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.skillSpinner.adapter = skillsSpinnerAdapter

        userViewModel.liveUser.observe(viewLifecycleOwner) {
            skillsSpinnerAdapter.clear()
            skillsSpinnerAdapter.addAll(it.skills)
            val initialPos = it.skills.indexOf(initialAdvSkill)
            if (initialPos != -1)
                binding.skillSpinner.setSelection(initialPos, true)
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    saveAndExit()
                }
            })

        binding.textInputEditDate.setOnClickListener {
            getDateTimeCalendar()
            DatePickerDialog(this.requireContext(),this,savedYear,savedMonth,savedDay).show()
        }

        binding.editAdvertisementSaveButton.setOnClickListener {
            saveAndExit()
        }

        return root
    }

    private fun checkSave(): Boolean {
        var check = true
        if( binding.textInputEditTitle.text.toString().isEmpty() ||
            binding.textInputEditDescription.text.toString().isEmpty() ||
            binding.textInputEditLocation.text.toString().isEmpty() ||
            binding.textInputEditDuration.text.toString().isEmpty() ||
            binding.textInputEditDate.text.toString().isEmpty()
        )
        check = false
        return check
    }

    private fun allEmpty() : Boolean {
        return binding.textInputEditTitle.text.toString().isEmpty() &&
                binding.textInputEditDescription.text.toString().isEmpty() &&
                binding.textInputEditLocation.text.toString().isEmpty() &&
                binding.textInputEditDuration.text.toString().isEmpty() &&
                binding.textInputEditDate.text.toString().isEmpty()
    }

    fun saveAndExit() {
        if (allEmpty()) {
            // Used to allow to go back on unintentional FAB presses
            findNavController().popBackStack()
        }
        else if(!checkSave())
            Snackbar
                .make(binding.root, "Please complete the form", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.RED)
                .show()
        else {

            val adv = Advertisement()

            adv.title = binding.textInputEditTitle.text.toString()
            adv.description = binding.textInputEditDescription.text.toString()
            adv.location = binding.textInputEditLocation.text.toString()
            adv.duration = binding.textInputEditDuration.text.toString()
            adv.date = binding.textInputEditDate.text.toString()
            adv.skill = binding.skillSpinner.selectedItem as? String ?: ""
            adv.emailCreator = userViewModel.liveUser.value!!.mail

            if (adv.skill.isEmpty()) {
                Snackbar.make(
                    binding.root,
                    "You must select a skill in order to proceed",
                    Snackbar.LENGTH_SHORT
                ).show()
                return;
            } else if (advID == null || advID == "") {
                advertisementViewModel.saveAdvertisement(adv)
            } else {
                advertisementViewModel.updateAdvertisement(advID!!, adv)
            }


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

        binding.textInputEditDate.setText(dateTimeToString(savedDay,savedMonth,savedYear,savedHour,savedMinute))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        saveAndExit()

        return true
    }

}