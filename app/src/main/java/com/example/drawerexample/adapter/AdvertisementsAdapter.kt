package com.example.drawerexample.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.drawerexample.Advertisement
import com.example.drawerexample.R

class AdvertisementsAdapter(private val parentFragment : Fragment, private val allowEdit : Boolean = false) : RecyclerView.Adapter<AdvertisementsAdapter.AdvViewHolder>() {

    var data = mutableListOf<Advertisement>()

    class AdvViewHolder(view: View, parentFragment: Fragment, myAdapter: AdvertisementsAdapter) : RecyclerView.ViewHolder(view) {
        val advTitleTv : TextView = view.findViewById(R.id.adv_title)
        val advDateTv : TextView = view.findViewById(R.id.adv_date)
        val advLocationTv : TextView = view.findViewById(R.id.adv_location)

        init {
            val bundle = Bundle()
            view.findViewById<ImageButton>(R.id.adv_edit).setOnClickListener {
                bundle.putString("advertisementID", myAdapter.data[adapterPosition].id)
                parentFragment.findNavController().navigate(R.id.action_nav_adv_list_to_nav_edit_adv, bundle)
            }

            // Listener to go see adv details
            view.findViewById<ConstraintLayout>(R.id.adv_card).setOnClickListener {
                bundle.apply {
                    putString("advertisementID", myAdapter.data[adapterPosition].id)
                    putBoolean("allowEdit", myAdapter.allowEdit)
                }
                parentFragment.findNavController().navigate(R.id.action_nav_adv_list_to_nav_show_adv, bundle)
            }

            val imageButton = view.findViewById<ImageButton>(R.id.adv_edit)
            when (myAdapter.allowEdit) {
                true -> { // In this case we should render the little pencil and bring to adv edit
                    imageButton
                        .apply { setImageResource(R.drawable.edit) }
                        .setOnClickListener {
                            bundle.putString("advertisementID", myAdapter.data[adapterPosition].id)
                            bundle.putBoolean("allowEdit", true)
                            parentFragment.findNavController().navigate(R.id.action_nav_adv_list_to_nav_edit_adv, bundle)
                        }

                }
                false -> { // In this case we should render the little user icon and go to show user profile
                    imageButton
                        .apply { setImageResource(R.drawable.user) }
                        .setOnClickListener {
                            bundle.putString("UID", myAdapter.data[adapterPosition].creatorUID)
                            parentFragment.findNavController().navigate(R.id.nav_show_profile, bundle)
                        }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.adv_list_item, parent, false)

        return AdvViewHolder(view, parentFragment, this)
    }


    override fun onBindViewHolder(holder: AdvViewHolder, position: Int) {
        holder.advTitleTv.text = data[position].title
        holder.advDateTv.text = data[position].date
        holder.advLocationTv.text = data[position].location
    }

    override fun getItemCount(): Int {
        return data.size
    }

}