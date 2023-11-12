package com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics.network

import android.net.Uri
import android.util.Log
import com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics.BuildConfig.GOOGLE_MAPS_API_KEY
import com.google.android.gms.maps.model.LatLng
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class NetworkUtils {
    companion object {
        val LOG_TAG = NetworkUtils::class.java.simpleName
        val NEARBYSEARCH_BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"

        fun getNearbyPlaces(latlng: LatLng): String {
            val buildURL = Uri.parse(NEARBYSEARCH_BASE_URL).buildUpon()
                .appendQueryParameter("location", "${latlng.latitude},${latlng.longitude}")
                .appendQueryParameter("radius", 1000.0.toString())
                .appendQueryParameter("key", GOOGLE_MAPS_API_KEY)
                .build()
            val requestURL = URL(buildURL.toString())
            val urlConnection = requestURL.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.connect()

            val isStream = urlConnection.inputStream
            val resultJson = isStream.bufferedReader().use(BufferedReader::readText)
            Log.d(LOG_TAG, resultJson)
            return resultJson
        }
    }

}