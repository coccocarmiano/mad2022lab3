package com.example.drawerexample.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.drawerexample.R

class SkillsAdapter(private var skills : HashMap<String, Boolean>) : RecyclerView.Adapter<SkillsAdapter.SkillViewHolder>() {

    class SkillViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val skillTextView : TextView = view.findViewById(R.id.skillsListItemTextView)
        val skillCheckBox : CheckBox = view.findViewById(R.id.skillsListItemCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.skill_list_item, parent, false)

        view.findViewById<CheckBox>(R.id.skillsListItemCheckbox).setOnClickListener {
            val skill = view.findViewById<TextView>(R.id.skillsListItemTextView).text.toString()
            skills[skill] = skills[skill]?.not() ?: false
        }

        return SkillViewHolder(view)
    }

    fun setData(skills: HashMap<String, Boolean>) {
        this.skills = skills
    }

    fun getData(): HashMap<String, Boolean> {
        return skills
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        val element = skills.entries.elementAt(position)
        holder.skillTextView.text = element.key
        holder.skillCheckBox.isChecked = element.value
    }

    override fun getItemCount(): Int {
        return skills.size
    }

}