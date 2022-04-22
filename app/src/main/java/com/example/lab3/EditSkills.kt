package com.example.lab3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.edit_skills_fragment.view.*
import kotlinx.android.synthetic.main.skill_list_item.view.*

class EditSkills : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()

    class SkillsAdapter(private val skills: Array<String>, private val userSkills : MutableLiveData<MutableList<String>> ) : RecyclerView.Adapter<SkillsAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val skillTextView : TextView = view.skillsListItemTextView
            val skillCheckBox : CheckBox = view.skillsListItemCheckbox
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.skill_list_item, parent, false)

            view.skillsListItemCheckbox.setOnClickListener {
                when (view.skillsListItemCheckbox.isChecked) {
                    true -> userSkills.value?.add(view.skillsListItemTextView.text.toString())
                    false -> userSkills.value?.remove(view.skillsListItemTextView.text.toString())
                }
            }

                return ViewHolder(view)
            }


        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.skillTextView.text = skills[position]
            holder.skillCheckBox.isChecked = userSkills.value?.contains(skills[position]) ?: false
        }

        override fun getItemCount(): Int {
            return skills.size
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.edit_skills_fragment, container, false)
        val skillsAdapter = SkillsAdapter(
                listOf(
                    "Gardening", "App Development", "Cooking", "Programming", "Reading",
                ).toTypedArray(), userViewModel.skills)
        val recyclerView = view.skillsListRecyclerView
        recyclerView.run {
            adapter = skillsAdapter
            layoutManager = LinearLayoutManager(container?.context)
        }



        view.saveSkillButton.setOnClickListener {
            activity?.onBackPressed()
        }

        return view
    }
}