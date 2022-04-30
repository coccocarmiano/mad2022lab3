package com.example.drawerexample.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.drawerexample.R

class SkillsAdapter(val skills: Array<String>, private val userSkills : MutableLiveData<MutableList<String>>) : RecyclerView.Adapter<SkillsAdapter.SkillViewHolder>() {

    class SkillViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val skillTextView : TextView = view.findViewById<TextView>(R.id.skillsListItemTextView)
        val skillCheckBox : CheckBox = view.findViewById<CheckBox>(R.id.skillsListItemCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.skill_list_item, parent, false)

        view.findViewById<CheckBox>(R.id.skillsListItemCheckbox).setOnClickListener {
            when (view.findViewById<CheckBox>(R.id.skillsListItemCheckbox).isChecked) {
                true -> userSkills.value?.add(view.findViewById<TextView>(R.id.skillsListItemTextView).text.toString())
                false -> userSkills.value?.remove(view.findViewById<TextView>(R.id.skillsListItemTextView).text.toString())
            }
        }

        return SkillViewHolder(view)
    }


    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        holder.skillTextView.text = skills[position]
        holder.skillCheckBox.isChecked = userSkills.value?.contains(skills[position]) ?: false
    }

    override fun getItemCount(): Int {
        return skills.size
    }

}