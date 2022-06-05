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
import com.example.drawerexample.databinding.EditTimeSlotDetailsFragmentBinding
import com.example.drawerexample.viewmodel.AdvertisementViewModel
import com.example.drawerexample.viewmodel.UserViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class EditAdvertisement : Fragment(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private val advertisementViewModel: AdvertisementViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    private lateinit var binding : EditTimeSlotDetailsFragmentBinding

    private var initialAdvSkill : String? = null

    private var savedDay = 0
    private var savedMonth = 0
    private var savedYear = 0
    private var savedHour = 0
    private var savedMinute = 0

    private lateinit var skillsSpinnerAdapter: ArrayAdapter<String>
    private var allowEdit = false
    private lateinit var advID : String
    private var oldAdv : Advertisement? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = EditTimeSlotDetailsFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        allowEdit = arguments?.getBoolean("allowEdit") ?: false
        advID = arguments?.getString("advertisementID", "").orEmpty()

        setHasOptionsMenu(allowEdit)


        if (advID.isNotEmpty()) {
            oldAdv = advertisementViewModel.liveAdvList.value?.find { it.id == advID }

            oldAdv.apply {
                binding.textInputEditTitle.setText(this?.title)
                binding.textInputEditDescription.setText(this?.description)
                binding.textInputEditLocation.setText(this?.location)
                binding.textInputEditDuration.setText(this?.duration)
                binding.textInputEditDate.setText(this?.date)
                initialAdvSkill = this?.skill
            }
        }

        skillsSpinnerAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, mutableListOf())
        skillsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.skillSpinner.adapter = skillsSpinnerAdapter

        userViewModel.skills.observe(viewLifecycleOwner) { userSkillList ->
            skillsSpinnerAdapter.clear()
            skillsSpinnerAdapter.addAll(userSkillList)
            val initialPos = userSkillList.indexOf(initialAdvSkill)
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

            var newAdv : Advertisement = if (oldAdv == null) {
                Advertisement()
            } else {
                oldAdv!!
            }

            newAdv.apply {
                title = binding.textInputEditTitle.text.toString()
                description = binding.textInputEditDescription.text.toString()
                location = binding.textInputEditLocation.text.toString()
                duration = binding.textInputEditDuration.text.toString()
                date = binding.textInputEditDate.text.toString()
                skill = binding.skillSpinner.selectedItem as? String ?: ""
            }

            when {
                newAdv.skill.isEmpty() -> {
                    Snackbar.make(
                        binding.root,
                        "You must select a skill in order to proceed",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    return
                }
                advID.isEmpty() -> {
                    newAdv.apply {
                        creatorMail = userViewModel.email.value ?: "_"
                        creatorUID = userViewModel.userID.value ?: "_"
                        status = "new"
                    }

                    advertisementViewModel.createAdvertisement(newAdv)
                }
                else -> advID.also { _ ->
                    advertisementViewModel.updateAdvertisement(advID, newAdv)
                }
            }
            findNavController().popBackStack()
        }
    }

    private fun dateTimeToString(day: Int, month: Int, year: Int,hour:Int, minute:Int): String{
        val date = Calendar.getInstance().apply { set(year, month, day, hour, minute) }
        val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return fmt.format(date.time)
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