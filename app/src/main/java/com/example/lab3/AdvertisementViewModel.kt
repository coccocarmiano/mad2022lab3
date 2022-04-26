package com.example.lab3

import android.app.Application
import android.content.Context
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.edit_time_slot_details_fragment.view.*
import kotlinx.android.synthetic.main.show_time_slot_details_fragment.view.*
import org.json.JSONObject

class AdvertisementViewModel(private val app: Application): AndroidViewModel(app) {
    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val date = MutableLiveData<String>()
    val duration = MutableLiveData<String>()
    val location = MutableLiveData<String>()
    init{
        val jsonString= app.getSharedPreferences("advertisement", Context.MODE_PRIVATE).getString("advertisement","{}")?:"{}"
        val json = JSONObject(jsonString)

        json.apply {
            title.value = optString("title", app.getString(R.string.title_advertisement_placeholder_text))
            description.value = optString("description", app.getString(R.string.description_advertisement_placeholder_text))
            date.value = optString("data", app.getString(R.string.data_advertisement_placeholder_text))
            duration.value = optString("duration", app.getString(R.string.duration_advertisement_placeholder_text))
            location.value = optString("location", app.getString(R.string.location_advertisement_placeholder_text))
        }
    }
    private fun save() {
        JSONObject()
            .apply {
                put("title", title.value)
                put("description", description.value)
                put("data", date.value)
                put("duration", duration.value)
                put("location", location.value)
            }.toString()
            .run {
                app
                    .getSharedPreferences("advertisement", Context.MODE_PRIVATE)
                    .edit()
                    .putString("advertisement", this)
                    .apply()
            }
    }

    fun updateFromEditAdvertisement(view: View){
        title.value = view.titleAdvertisementET.text.toString()
        description.value = view.descriptionAdvertisementET.text.toString()
        location.value = view.locationAdvertisementET.text.toString()
        duration.value = view.durationAdvertisementET.text.toString()
        date.value = view.dateAdvertisementEditTV.text.toString()
        save()
    }

}
