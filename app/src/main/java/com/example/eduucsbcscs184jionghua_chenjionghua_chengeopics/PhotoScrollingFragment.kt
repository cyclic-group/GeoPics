package com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics.BuildConfig.GOOGLE_MAPS_API_KEY
import com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics.databinding.FragmentLoginBinding
import com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics.databinding.FragmentPhotoScrollingBinding
import com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics.model.Place
import com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics.network.NetworkUtils
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import java.lang.Exception
import java.lang.Math.*

class PhotoScrollingFragment : Fragment(), LoaderManager.LoaderCallbacks<String> {

    private var _binding: FragmentPhotoScrollingBinding? = null
    private val binding get() = _binding!!
    private lateinit var pos: LatLng

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhotoScrollingBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Nearby Places"

        binding.recyclerView.adapter = PlaceListAdapter(requireContext(),ArrayList<Place>())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())


        if (LoaderManager.getInstance(this).getLoader<String>(0) != null) {
            LoaderManager.getInstance(this).initLoader(0,null, this)
        }

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->

            pos = LatLng(location.latitude,location.longitude)

            val queryBundle = Bundle()
            queryBundle.putDouble("lat", location.latitude)
            queryBundle.putDouble("lng", location.longitude)
            LoaderManager.getInstance(this).restartLoader(0, queryBundle, this)
        }
    }

    override fun onCreateLoader(id: Int, bundle: Bundle?): Loader<String> {
        val lat = bundle?.getDouble("lat")
        val lng = bundle?.getDouble("lng")
        return PlacesLoader(requireActivity(),LatLng(lat!!,lng!!))
    }

    override fun onLoadFinished(loader: Loader<String>, result: String?) {
        val nearbyPlaces = JSONObject(result).getJSONArray("results")
        val placesList = ArrayList<Place>()
        for (i in 0 until nearbyPlaces.length()) {
            try {
                val place = nearbyPlaces.getJSONObject(i)
                val title = place.getString("name")

                val photos = place.getJSONArray("photos")

                if (photos.length() == 0) {
                    continue
                }
                val photoRef = photos.getJSONObject(0).getString("photo_reference")
                val lat = place.getJSONObject("geometry").getJSONObject("location").getDouble("lat")
                val lng = place.getJSONObject("geometry").getJSONObject("location").getDouble("lng")

                val imageURL =
                    "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photo_reference=$photoRef&key=$GOOGLE_MAPS_API_KEY"
                val lat0 = pos.latitude
                val lng0 = pos.longitude
                val distance =
                    3963.0 * acos(sin(lat0) * sin(lat) + cos(lat0) * cos(lat) * cos(lng - lng0))
                val placeObject = Place(title, imageURL, distance)
                placesList.add(placeObject)
            } catch (e: Exception) {
                continue
            }
        }
        (binding.recyclerView.adapter as PlaceListAdapter).setList(placesList)
        (binding.recyclerView.adapter as PlaceListAdapter).notifyDataSetChanged()

    }

    override fun onLoaderReset(loader: Loader<String>) {

    }
}