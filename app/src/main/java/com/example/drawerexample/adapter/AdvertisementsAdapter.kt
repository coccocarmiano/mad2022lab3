package com.example.drawerexample.adapter

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.drawerexample.Advertisement
import com.example.drawerexample.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AdvertisementsAdapter(private val parentFragment : Fragment, private val allowEdit : Boolean = false, private val type : String) : RecyclerView.Adapter<AdvertisementsAdapter.AdvViewHolder>() {

    var data = mutableListOf<Advertisement>()

    class AdvViewHolder(view: View, parentFragment: Fragment, myAdapter: AdvertisementsAdapter) : RecyclerView.ViewHolder(view) {
        val advTitleTv : TextView = view.findViewById(R.id.adv_title)
        val advDateTv : TextView = view.findViewById(R.id.adv_date)
        val advLocationTv : TextView = view.findViewById(R.id.adv_location)

        init {
            val bundle = Bundle()
            // Listener to go see adv details
            view.findViewById<ConstraintLayout>(R.id.adv_card).setOnClickListener {
                bundle.apply {
                    putString("advertisementID", myAdapter.data[adapterPosition].id)
                    putBoolean("allowEdit", myAdapter.allowEdit)
                }
                parentFragment.findNavController().navigate(R.id.action_nav_adv_list_to_nav_show_adv, bundle)
            }

            val primaryImageButton = view.findViewById<ImageButton>(R.id.adv_primary_btn)
            val secondaryImageButton = view.findViewById<ImageButton>(R.id.adv_secondary_btn)
            when (myAdapter.type) {
                "done" -> {
                    primaryImageButton
                        .apply { setImageResource(R.drawable.user) }
                        .setOnClickListener {
                            if(myAdapter.data[adapterPosition].creatorUID==Firebase.auth.currentUser?.uid){
                                bundle.putString("UID", myAdapter.data[adapterPosition].buyerUID)
                                //TODO togliere da on click listener
                                primaryImageButton.visibility= View.GONE

                            }
                            else {
                                bundle.putString("UID", myAdapter.data[adapterPosition].creatorUID)
                                parentFragment.findNavController().navigate(R.id.nav_adv_list_to_show_other_profile, bundle)
                            }
                        }
                    secondaryImageButton
                        .apply { setImageResource(R.drawable.star_rate) }
                        .setOnClickListener {
                            var UID : String = if(myAdapter.data[adapterPosition].creatorUID==Firebase.auth.currentUser?.uid){
                                myAdapter.data[adapterPosition].buyerUID
                            }
                            else {
                                myAdapter.data[adapterPosition].creatorUID
                            }

                            val dialogBuilder = AlertDialog.Builder(parentFragment.context)
                            val dialogView = parentFragment.layoutInflater.inflate(R.layout.dialog_box_user_rating, null)
                            val closeDialogButton = dialogView.findViewById<ImageButton>(R.id.close_btn)
                            val rateButton = dialogView.findViewById<ImageButton>(R.id.rate_btn)

                            dialogBuilder.setView(dialogView)
                            val dialog = dialogBuilder.create()
                            dialog.setCanceledOnTouchOutside(true)

                            closeDialogButton.setOnClickListener {
                                dialog.cancel()
                            }
                            rateButton.setOnClickListener {
                                val ratingBar = dialogView.findViewById<RatingBar>(R.id.user_rating_bar)
                                val commentEditText = dialogView.findViewById<TextInputEditText>(R.id.textInputEditComment)

                                val rating = ratingBar.rating
                                val comment = commentEditText.text.toString()

                                // todo save data on db
                            }
                            dialog.show()
                        }
                }
                else -> {
                    when (myAdapter.allowEdit) {
                        true -> { // In this case we should render the little pencil and bring to adv edit
                            primaryImageButton
                                .apply { setImageResource(R.drawable.edit) }
                                .setOnClickListener {
                                    bundle.putString("advertisementID", myAdapter.data[adapterPosition].id)
                                    bundle.putBoolean("allowEdit", true)
                                    parentFragment.findNavController().navigate(R.id.action_nav_adv_list_to_nav_edit_adv, bundle)
                                }
                            secondaryImageButton.visibility = View.GONE

                        }
                        false -> { // In this case we should render the little user icon and go to show user profile
                            primaryImageButton
                                .apply { setImageResource(R.drawable.user) }
                                .setOnClickListener {
                                    if(myAdapter.data[adapterPosition].creatorUID==Firebase.auth.currentUser?.uid){
                                        bundle.putString("UID", myAdapter.data[adapterPosition].buyerUID)
                                        //TODO togliere da on click listener
                                        primaryImageButton.visibility= View.GONE

                                    }
                                    else {
                                        bundle.putString("UID", myAdapter.data[adapterPosition].creatorUID)
                                        parentFragment.findNavController().navigate(R.id.nav_adv_list_to_show_other_profile, bundle)
                                    }
                                }
                            secondaryImageButton.setOnClickListener {
                                bundle.apply {
                                    if(myAdapter.data[adapterPosition].creatorUID==Firebase.auth.currentUser?.uid) {
                                        //TODO qua si perde il nome, da sistemare
                                        putString("otherUserID", myAdapter.data[adapterPosition].buyerUID)
                                        putString("advertisementID", myAdapter.data[adapterPosition].id)
                                        putString("userID", Firebase.auth.currentUser?.uid)
                                    }
                                    else{
                                        putString("otherUserID", myAdapter.data[adapterPosition].creatorUID)
                                        putString("advertisementID", myAdapter.data[adapterPosition].id)
                                        putString("userID", Firebase.auth.currentUser?.uid)
                                    }
                                    parentFragment.findNavController().navigate(R.id.action_nav_adv_list_to_chat, bundle)
                                }
                            }
                        }
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