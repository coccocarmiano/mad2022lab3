package com.example.drawerexample.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.drawerexample.Advertisement
import com.example.drawerexample.R
import com.example.drawerexample.viewmodel.AdvertisementViewModel


class AdvertisementsAdapterNoEdit(private val parentFragment : Fragment) : RecyclerView.Adapter<AdvertisementsAdapterNoEdit.AdvViewHolder>() {

    var data = mutableListOf<Advertisement>()

    class AdvViewHolder(view: View, parentFragment: Fragment, myAdapter: AdvertisementsAdapterNoEdit) : RecyclerView.ViewHolder(view) {
        val advTitleTv : TextView = view.findViewById(R.id.adv_titleNoEdit)
        val advDateTv : TextView = view.findViewById(R.id.adv_dateNoEdit)
        val advLocationTv : TextView = view.findViewById(R.id.adv_locationNoEdit)
        val advModel: AdvertisementViewModel by parentFragment.viewModels<AdvertisementViewModel>()

        init {
            view.findViewById<ImageButton>(R.id.adv_edit).setOnClickListener {
                val bundle = Bundle()
                bundle.putString("adv_ID", myAdapter.data[adapterPosition].id)
                advModel.findCreator(myAdapter.data[adapterPosition].emailCreator)
                parentFragment.findNavController().navigate(R.id.nav_adv_list_to_show_other_profile, bundle)
            }


            view.findViewById<ConstraintLayout>(R.id.adv_card).setOnClickListener {
                val bundle = Bundle()
                bundle.putString("adv_ID", myAdapter.data[adapterPosition].id)
                parentFragment.findNavController().navigate(R.id.action_nav_adv_list_to_nav_show_adv, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdvViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.adv_list_item_no_edit, parent, false)

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