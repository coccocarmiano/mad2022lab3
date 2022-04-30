package com.example.drawerexample.viewmodel

import android.app.Application
import android.content.Context
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.drawerexample.Advertisement
import com.example.drawerexample.R
import com.example.drawerexample.UserProfile
import org.json.JSONArray
import org.json.JSONObject

class AdvertisementViewModel(private val app: Application): AndroidViewModel(app) {

    var observer : Observer<MutableList<Advertisement>>
    val liveAdvList = MutableLiveData<MutableList<Advertisement>>()

    init{
        val jsonString= app.getSharedPreferences("advertisements", Context.MODE_PRIVATE).getString("advertisements","[]")?:"[]"
        val json = JSONArray(jsonString)

        val advList = mutableListOf<Advertisement>()
        for (i in 0 until json.length()) {
            val jsonAdv = json.getJSONObject(i)

            val adv = Advertisement()
            jsonAdv.apply {
                adv.title = optString("title", app.getString(R.string.title_advertisement_placeholder_text))
                adv.description = optString("description", app.getString(R.string.description_advertisement_placeholder_text))
                adv.date = optString("date", app.getString(R.string.date_advertisement_placeholder_text))
                adv.duration = optString("duration", app.getString(R.string.duration_advertisement_placeholder_text))
                adv.location = optString("location", app.getString(R.string.location_advertisement_placeholder_text))
            }

            advList.add(adv)
        }

        liveAdvList.value = advList

        observer = Observer { save(it)}
        liveAdvList.observeForever(observer)
    }

    private fun save(list : MutableList<Advertisement>) {
        val data = JSONArray()
        for (adv in list) {
            val jsonObj = JSONObject()
            jsonObj.apply {
                put("title", adv.title)
                put("description", adv.description)
                put("date", adv.date)
                put("duration", adv.duration)
                put("location", adv.location)
            }
            data.put(jsonObj)
        }

        app
            .getSharedPreferences("advertisements", Context.MODE_PRIVATE)
            .edit()
            .putString("advertisements", data.toString())
            .apply()
    }

    override fun onCleared() {
        liveAdvList.removeObserver(observer)
        super.onCleared()
    }

}
