package com.example.drawerexample.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.DatePicker
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drawerexample.R
import com.example.drawerexample.adapter.AdvertisementsAdapter
import com.example.drawerexample.databinding.FragmentAdvListBinding
import com.example.drawerexample.viewmodel.AdvertisementViewModel
import com.example.drawerexample.viewmodel.UserViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*


class AdvListFragment : Fragment(), DatePickerDialog.OnDateSetListener {

    private val advViewModel: AdvertisementViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var binding : FragmentAdvListBinding

    private lateinit var selectedSkill : String
    private var filtersOpened: Boolean = false

    private lateinit var advAdapter: AdvertisementsAdapter

    private var titleFilter: String = ""
    private var locationFilter: String = ""
    private var dateFilter: String = ""

    private var dateFiltered:Boolean = false

    private var sortingMode = "Date" // Allowed: "Date", "Title"
    private var allowEdit : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdvListBinding.inflate(inflater, container, false)
        val root: View = binding.root
        allowEdit = arguments?.getBoolean("allowEdit") ?: false
        selectedSkill = arguments?.getString("selectedSkill") ?: "NO_SKILL_SELECTED"

        advAdapter = AdvertisementsAdapter(this, allowEdit = allowEdit)

        binding.advListRv.apply {
            adapter = advAdapter
            layoutManager = LinearLayoutManager(context)
        }

        userViewModel.userID.observe(viewLifecycleOwner) {
            advViewModel.liveAdvList.value = advViewModel.liveAdvList.value
        }

        advViewModel.liveAdvList.observe(viewLifecycleOwner) { advList ->
            when {
                advList.isNullOrEmpty() -> binding.noAdvTV.visibility = View.VISIBLE
                else -> {
                    binding.noAdvTV.visibility = View.GONE
                    refreshUI()
                }
            }

        }

        binding.addAdvertisementFAB.setOnClickListener {
            val bundle = Bundle().apply { putBoolean("allowEdit", true) }
            findNavController().navigate(R.id.action_nav_adv_list_to_nav_edit_adv, bundle)
        }

        binding.filtersDropdownBtn.setOnClickListener {
            when (filtersOpened) {
                true -> {
                    binding.filtersDropdownBtn.setImageResource(R.drawable.arrow_drop_up)
                    binding.filtersBody.visibility = View.VISIBLE
                    filtersOpened = false
                }
                false -> {
                    binding.filtersDropdownBtn.setImageResource(R.drawable.arrow_drop_down)
                    binding.filtersBody.visibility = View.GONE
                    filtersOpened = true
                }
            }
        }

        binding.textInputEditTitle.doOnTextChanged { text, _, _, _ ->
            titleFilter = text.toString()
            refreshUI()
        }

        binding.textInputEditLocation.doOnTextChanged { text, _, _, _ ->
            locationFilter = text.toString()
            refreshUI()
        }

        binding.textInputEditDate.setOnClickListener {
            //TODO: What do these do?
            binding.textInputEditTitle.clearFocus()
            binding.textInputEditLocation.clearFocus()

            when (dateFiltered) {
                true -> {
                    dateFiltered = false
                    binding.textInputEditDate.setText("")
                    dateFilter = ""
                    refreshUI()
                }
                false -> {
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(
                        this.requireContext(),
                        this,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            }

        }

        binding.sortBtn.setOnClickListener {
            sortingMode = when (sortingMode) {
                "Title" -> "Date"
                "Date" -> "Title"
                else -> "Date"
            }
            refreshUI()
        }

        binding.addAdvertisementFAB.visibility = if (allowEdit) View.VISIBLE else View.GONE
        return root
    }

    private fun dateToString(day: Int, month: Int, year: Int): String{
        val date = Calendar.getInstance().apply { set(year, month, day) }
        val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return fmt.format(date.time)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refreshUI() {
        var filteredAdv = advViewModel.liveAdvList.value ?: mutableListOf()
        val currentUserUID = Firebase.auth.currentUser?.uid
        filteredAdv = filteredAdv
            .asSequence()
            .filter { it.title.contains(titleFilter, ignoreCase = true) }
            .filter { it.location.contains(locationFilter, ignoreCase = true) }
            .filter { it.date.contains(dateFilter, ignoreCase = true) }
            .filter {
                when (allowEdit)  {
                    true -> true
                    else -> it.skill == selectedSkill
                }
            }
            .filter {
                when (allowEdit) {
                    true -> it.creatorUID == currentUserUID
                    false -> it.creatorUID != currentUserUID
                }
            }
            .sortedBy {
                when (sortingMode) {
                    "Date" -> it.date
                    else -> it.title
                }
            }
            .toMutableList() //TODO Replace with UID...


        //TODO: Add resource for this
        binding.sortLabel.text = getString(R.string.sorted_by_literal).format(sortingMode)

        advAdapter.data = filteredAdv
        advAdapter.notifyDataSetChanged()

        when (filteredAdv.isEmpty()) {
            true -> binding.noAdvTV.visibility = View.VISIBLE
            false -> binding.noAdvTV.visibility = View.GONE
        }
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        // TODO: Add resource for this
        val stringDate = dateToString(p3, p2, p1)
        val str = getString(R.string.tap_to_remove_adv_list_fragment).format(stringDate)
        binding.textInputEditDate.setText(str)
        dateFilter = stringDate
        refreshUI()
        dateFiltered = true
    }
}


