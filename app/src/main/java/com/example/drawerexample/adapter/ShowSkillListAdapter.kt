package com.example.drawerexample.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.drawerexample.R

class ShowSkillListAdapter(private val parentFragment: Fragment) : RecyclerView.Adapter<ShowSkillListAdapter.SkillViewHolder>()  {

    var data = listOf<String>()

    class SkillViewHolder(view: View, parentFragment: Fragment, myAdapter: ShowSkillListAdapter) : RecyclerView.ViewHolder(view) {
        val skillTitleTv : TextView = view.findViewById(R.id.skill_title)

        init {
            view.findViewById<ConstraintLayout>(R.id.skill_card).setOnClickListener {
                val bundle = Bundle()
                bundle.putString("skill", myAdapter.data[adapterPosition])
                parentFragment.findNavController().navigate(R.id.action_show_skills_list_to_nav_adv_list, bundle)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkillViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_show_skills_list, parent, false)

        return SkillViewHolder(view, parentFragment, this)
    }

    override fun onBindViewHolder(holder: SkillViewHolder, position: Int) {
        holder.skillTitleTv.text = data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }
}