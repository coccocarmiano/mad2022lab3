package com.example.drawerexample.adapter

import android.app.AlertDialog
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.drawerexample.Advertisement
import com.example.drawerexample.R
import com.example.drawerexample.ui.AdvListFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AdvertisementsAdapter(private val parentFragment : Fragment, private val type : String = "") : RecyclerView.Adapter<AdvertisementsAdapter.AdvViewHolder>() {

    var data = mutableListOf<Advertisement>()

    class AdvViewHolder(view: View, parentFragment: Fragment, myAdapter: AdvertisementsAdapter) : RecyclerView.ViewHolder(view) {
        val advTitleTv : TextView = view.findViewById(R.id.adv_title)
        val advDateTv : TextView = view.findViewById(R.id.adv_date)
        val advLocationTv : TextView = view.findViewById(R.id.adv_location)
        val requestsCounter : TextView = view.findViewById(R.id.requests_counter)
        val primaryImageButton : ImageButton = view.findViewById(R.id.adv_primary_btn)
        val secondaryImageButton : ImageButton = view.findViewById(R.id.adv_secondary_btn)

        init {
            // Listener to go see adv details
            view.findViewById<ConstraintLayout>(R.id.adv_card).setOnClickListener {
                val bundle = Bundle()
                bundle.apply {
                    putString("advertisementID", myAdapter.data[adapterPosition].id)
                    var allowEdit = myAdapter.type == "my"
                    putBoolean("allowEdit", allowEdit)
                }
                parentFragment.findNavController().navigate(R.id.action_nav_adv_list_to_nav_show_adv, bundle)
            }

            requestsCounter.visibility = View.GONE
            when (myAdapter.type) {
                "my"    -> {
                    primaryImageButton
                        .apply { setImageResource(R.drawable.edit) }
                        .setOnClickListener {
                            if (myAdapter.data[adapterPosition].requests.isNotEmpty()) {
                                (parentFragment as AdvListFragment).showSnackBarMessage(parentFragment.resources.getString(R.string.no_edit_if_requests), err = true)
                            } else {
                                val bundle = Bundle()
                                bundle.putString(
                                    "advertisementID",
                                    myAdapter.data[adapterPosition].id
                                )
                                bundle.putBoolean("allowEdit", true)
                                parentFragment.findNavController()
                                    .navigate(R.id.action_nav_adv_list_to_nav_edit_adv, bundle)
                            }
                        }
                    secondaryImageButton.visibility = View.GONE
                }
                "accepted"  -> {
                    primaryImageButton
                        .apply { setImageResource(R.drawable.user) }
                        .setOnClickListener {
                            val bundle = Bundle()
                            bundle.putString("UID", myAdapter.data[adapterPosition].creatorUID)
                            parentFragment.findNavController().navigate(R.id.nav_adv_list_to_show_other_profile, bundle)
                        }
                }
                else -> {
                    primaryImageButton
                        .apply { setImageResource(R.drawable.user) }
                        .setOnClickListener {
                            val bundle = Bundle()
                            bundle.putString("UID", myAdapter.data[adapterPosition].creatorUID)
                            parentFragment.findNavController().navigate(R.id.nav_adv_list_to_show_other_profile, bundle)
                        }
                    secondaryImageButton
                        .apply { setImageResource(R.drawable.letter) }
                        .setOnClickListener {
                            val bundle = Bundle()
                            bundle.apply {
                                if (myAdapter.data[adapterPosition].creatorUID == Firebase.auth.currentUser?.uid) {
                                    //TODO qua si perde il nome, da sistemare
                                    putString("otherUserID", myAdapter.data[adapterPosition].buyerUID)
                                    putString("advertisementID", myAdapter.data[adapterPosition].id)
                                    putString("userID", Firebase.auth.currentUser?.uid)
                                } else {
                                    putString("otherUserID", myAdapter.data[adapterPosition].creatorUID)
                                    putString("advertisementID", myAdapter.data[adapterPosition].id)
                                    putString("userID", Firebase.auth.currentUser?.uid)
                                }
                            }
                            parentFragment.findNavController().navigate(R.id.action_nav_adv_list_to_chat, bundle)
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

        if (type == "my") {
            val requestsCount = data[position].requests.size
            if (requestsCount > 0) {
                holder.requestsCounter.visibility = View.VISIBLE
                if (requestsCount == 1)
                    holder.requestsCounter.text = requestsCount.toString() + " " + parentFragment.resources.getString(R.string.request)
                else
                    holder.requestsCounter.text = requestsCount.toString() + " " + parentFragment.resources.getString(R.string.requests)
            } else {
                holder.requestsCounter.visibility = View.GONE
            }
        }

        if (type == "accepted") {
            val fmt = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val advDate = fmt.parse(data[position].date)
            val advDuration = data[position].duration.toLongOrNull()
            val currentDate = Calendar.getInstance().time

            var canRateAdv = false
            if (advDate != null && advDuration != null) {
                var millisTime = advDate.time
                millisTime += 1000 * 60 * advDuration
                val advEndingDate = Date(millisTime)

                if (currentDate.after(advEndingDate)) {
                    canRateAdv = true
                }
            }

            var roleAlreadyRated = false
            if (canRateAdv) {
                var roleToRate: String =
                    if (data[position].creatorUID == Firebase.auth.currentUser?.uid) {
                        "buyer"
                    } else {
                        "seller"
                    }
                when (roleToRate) {
                    "buyer" -> {
                        if (data[position].rateForBuyer != null)
                            roleAlreadyRated = true
                    }
                    "seller" -> {
                        if (data[position].rateForSeller != null)
                            roleAlreadyRated = true
                    }
                }
            }

            if (canRateAdv && roleAlreadyRated)
                holder.secondaryImageButton.visibility = View.GONE

            holder.secondaryImageButton
                .apply {
                    if (canRateAdv)
                        setImageResource(R.drawable.star_rate)
                    else
                        setImageResource(R.drawable.letter)
                }
                .setOnClickListener {
                    if (canRateAdv) {
                        var roleToRate: String =
                            if (data[position].creatorUID == Firebase.auth.currentUser?.uid) {
                                "buyer"
                            } else {
                                "seller"
                            }
                        val dialogBuilder = AlertDialog.Builder(parentFragment.context)
                        val dialogView = parentFragment.layoutInflater.inflate(
                            R.layout.dialog_box_user_rating,
                            null
                        )
                        val closeDialogButton = dialogView.findViewById<ImageButton>(R.id.close_btn)
                        val rateButton = dialogView.findViewById<Button>(R.id.rate_btn)
                        val ratingBoxHeader : TextView = dialogView.findViewById(R.id.rating_box_header)

                        dialogBuilder.setView(dialogView)
                        val dialog = dialogBuilder.create()
                        dialog.setCanceledOnTouchOutside(true)

                        //ratingBoxHeader.visibility = View.GONE
                        val roleToRateCapital = roleToRate.replaceFirst(roleToRate.toCharArray()[0], roleToRate.toCharArray()[0].uppercaseChar())
                        ratingBoxHeader.text = parentFragment.resources.getString(R.string.rate_your) + " " + roleToRateCapital

                        closeDialogButton.setOnClickListener {
                            dialog.cancel()
                        }
                        rateButton.setOnClickListener {
                            val ratingBar =
                                dialogView.findViewById<RatingBar>(R.id.user_rating_bar)
                            val commentEditText =
                                dialogView.findViewById<TextInputEditText>(R.id.textInputEditComment)

                            val rating = ratingBar.rating
                            val comment = commentEditText.text.toString()

                            if (rating == 0f) {
                                Snackbar.make(dialogView, parentFragment.resources.getString(R.string.rating_invalid), Snackbar.LENGTH_LONG)
                                    .setBackgroundTint(Color.RED)
                                    .show()
                            } else {
                                when (roleToRate) {
                                    "buyer" -> {
                                        data[position].rateForBuyer = rating
                                        data[position].commentForBuyer = comment
                                    }
                                    "seller" -> {
                                        data[position].rateForSeller = rating
                                        data[position].commentForSeller = comment
                                    }
                                }

                                (parentFragment as AdvListFragment).updateAdv(data[position])
                                dialog.cancel()
                            }
                        }
                        dialog.show()
                    } else {
                        val bundle = Bundle()
                        bundle.apply {
                            if (data[position].creatorUID == Firebase.auth.currentUser?.uid) {
                                //TODO qua si perde il nome, da sistemare
                                putString(
                                    "otherUserID",
                                    data[position].buyerUID
                                )
                                putString(
                                    "advertisementID",
                                    data[position].id
                                )
                                putString("userID", Firebase.auth.currentUser?.uid)
                            } else {
                                putString(
                                    "otherUserID",
                                    data[position].creatorUID
                                )
                                putString(
                                    "advertisementID",
                                    data[position].id
                                )
                                putString("userID", Firebase.auth.currentUser?.uid)
                            }
                        }
                        parentFragment.findNavController().navigate(R.id.action_nav_adv_list_to_chat, bundle)
                    }
                }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

}