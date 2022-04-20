package com.example.lab3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.edit_skills_fragment.view.*
import kotlinx.android.synthetic.main.skill_list_item.view.*

class EditSkills : Fragment() {

    private val userViewModel: UserViewModel by activityViewModels()

    class SkillsAdapter(private val skills: Array<String>) : RecyclerView.Adapter<SkillsAdapter.ViewHolder>() {

        class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            val skillTextView = view.skillsListItemTextView
            val skillCheckBox = view.skillsListItemCheckbox
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.skill_list_item, parent, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.skillTextView.text = skills[position]
            holder.skillCheckBox.isChecked = false
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
        val skillsAdapter = SkillsAdapter(listOf("Skill1", "Skill2", "Skill3").toTypedArray())
        val recyclerView = view.skillsListRecyclerView
        recyclerView.run {
            adapter = skillsAdapter
            layoutManager = LinearLayoutManager(container?.context)
        }

        return view;
    }
}