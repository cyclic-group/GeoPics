package com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class GoogleMapViewModel: ViewModel() {
    private val userRoute = ArrayList<LatLng>()

    fun addLocation(latlng: LatLng) {
        userRoute.add(latlng)
    }

    fun getRoute(): ArrayList<LatLng> {
        return userRoute
    }

    fun clearRoute() {
        userRoute.clear()
    }
}