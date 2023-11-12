package com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics

import android.content.Context
import androidx.loader.content.AsyncTaskLoader
import com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics.network.NetworkUtils
import com.google.android.gms.maps.model.LatLng

class PlacesLoader(context: Context, private val latLng: LatLng): AsyncTaskLoader<String>(context) {

    override fun onStartLoading() {
        super.onStartLoading()
        forceLoad()
    }

    override fun loadInBackground(): String? {
        return NetworkUtils.getNearbyPlaces(latLng)
    }
}