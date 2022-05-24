package com.example.drawerexample.ui

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawerexample.Advertisement
import com.example.drawerexample.R
import com.example.drawerexample.adapter.AdvertisementsAdapter
import com.example.drawerexample.adapter.AdvertisementsAdapterNoEdit
import com.example.drawerexample.viewmodel.AdvertisementViewModel
import com.example.drawerexample.databinding.FragmentAdvListBinding
import com.example.drawerexample.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

class MyAdvListFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private val advViewModel: AdvertisementViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var binding : FragmentAdvListBinding

    private var filtersOpened: Boolean = false
    private lateinit var advAdapter: AdvertisementsAdapter
    private var myAdvList: List<Advertisement> = listOf()
    private var titleFilter: String = ""
    private var locationFilter: String = ""
    private var dateFilter: String = ""

    private var dateFiltered:Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAdvListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        advAdapter = AdvertisementsAdapter(this)

        val recyclerView = binding.advListRv
        recyclerView.run {
            adapter = advAdapter
            layoutManager = LinearLayoutManager(container?.context)
        }

        userViewModel.liveUser.observe(viewLifecycleOwner) {
            advViewModel.liveAdvList.value = advViewModel.liveAdvList.value
        }

        advViewModel.liveAdvList.observe(viewLifecycleOwner) { advList ->
            if (advList != null) {
                if (advList.isEmpty()) {
                    binding.noAdvTV.visibility = View.VISIBLE
                } else {
                    binding.noAdvTV.visibility = View.GONE
                    val myAdvertisementList =
                        advList.filter { it.emailCreator == userViewModel.liveUser.value!!.mail }.let {
                            myAdvList = it
                            refreshUI()
                        }
                }
            }
        }

        binding.advAddFb.setOnClickListener {
            findNavController().navigate(R.id.action_nav_adv_myList_to_nav_edit_adv)
        }

        binding.filtersDropdownBtn.setOnClickListener {
            filtersOpened = !filtersOpened
            if (filtersOpened) {
                binding.filtersDropdownBtn.setImageResource(R.drawable.arrow_drop_up)
                binding.filtersBody.visibility = View.VISIBLE
            } else {
                binding.filtersDropdownBtn.setImageResource(R.drawable.arrow_drop_down)
                binding.filtersBody.visibility = View.GONE
            }
        }

        binding.textInputEditTitle.doOnTextChanged { text, start, before, count ->
            titleFilter = text.toString()
            refreshUI()
        }
        binding.textInputEditLocation.doOnTextChanged { text, start, before, count ->
            locationFilter = text.toString()
            refreshUI()
        }

        binding.textInputEditDate.setOnClickListener {
            binding.textInputEditTitle.clearFocus()
            binding.textInputEditLocation.clearFocus()

            if (dateFiltered) {
                dateFiltered = false
                binding.textInputEditDate.setText("")
                dateFilter = ""
                refreshUI()
            } else {
                val cal = Calendar.getInstance()
                DatePickerDialog(
                    this.requireContext(),
                    this,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finishAndRemoveTask()
                }
            })

        return root
    }

    private fun dateToString(day: Int, month: Int, year: Int): String{
        val c = Calendar.getInstance()

        c.set(year, month, day)

        val simpleDateTimeFormat = SimpleDateFormat("dd/MM/yyyy")

        return simpleDateTimeFormat.format(c.time)
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        var stringDate = dateToString(p3, p2, p1)
        binding.textInputEditDate.setText("$stringDate (Tap to remove)")
        dateFilter = stringDate
        refreshUI()
        dateFiltered = true
    }

    private fun refreshUI() {
        var filteredAdv = myAdvList

        filteredAdv = filteredAdv.filter { it.title.startsWith(titleFilter, ignoreCase = true) }
        filteredAdv = filteredAdv.filter { it.location.startsWith(locationFilter, ignoreCase = true) }
        filteredAdv = filteredAdv.filter { it.date.startsWith(dateFilter, ignoreCase = true) }

        advAdapter.data = filteredAdv.toMutableList()
        advAdapter.notifyDataSetChanged()

        if (filteredAdv.isEmpty()) {
            binding.noAdvTV.visibility = View.VISIBLE
        } else {
            binding.noAdvTV.visibility = View.GONE
        }
    }
}