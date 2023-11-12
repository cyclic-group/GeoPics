package com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics.model.Place
import org.w3c.dom.Text

class PlaceListAdapter(context: Context, places: ArrayList<Place>): RecyclerView.Adapter<PlaceListAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(itemView: View, val adapter: PlaceListAdapter): RecyclerView.ViewHolder(itemView) {
        val titleView = itemView.findViewById<TextView>(R.id.textview_title)
        val distanceView = itemView.findViewById<TextView>(R.id.textview_distance)
        val imageView = itemView.findViewById<ImageView>(R.id.imageview_place)
    }

    private val context = context
    private var places = places
    private val inflater = LayoutInflater.from(context)

    fun setList(places: ArrayList<Place>) {
        this.places = places
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val itemView = inflater.inflate(R.layout.place_item, parent, false)
        return PlaceViewHolder(itemView, this)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = places[position]
        holder.titleView.text = place.title
        holder.distanceView.text = "%.2f miles".format(place.distance)
        Glide.with(context)
            .load(place.imageURL)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return places.size
    }
}