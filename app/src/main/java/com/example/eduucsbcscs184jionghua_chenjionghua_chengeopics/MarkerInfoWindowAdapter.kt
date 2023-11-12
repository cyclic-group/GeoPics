package com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class MarkerInfoWindowAdapter(
    private val context: Context
) : GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(marker: Marker): View? {

//        if ((marker?.tag as String) == "user marker!") {
//
//        } else {

            // Inflate view and set title, address, and rating
            val view = LayoutInflater.from(context).inflate(
                R.layout.marker_info_content, null
            )
            //        view.findViewById<TextView>(
            //            R.id.text_view_title
            //        ).text = marker.title
            //        view.findViewById<TextView>(
            //            R.id.text_view_address
            //        ).text = marker.position.toString()
            val imageView = view.findViewById<ImageView>(R.id.imageView)
            val URI = Uri.parse(marker.tag as String)
            imageView.setImageURI(URI)

            return view
//        }
    }

    override fun getInfoWindow(marker: Marker): View? {
        // Return null to indicate that the
        // default window (white bubble) should be used
        return null
    }
}