package com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics.databinding.ActivityMapsBinding
import com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics.databinding.FragmentGoogleMapBinding
import com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics.databinding.FragmentLoginBinding
import com.example.eduucsbcscs184jionghua_chenjionghua_chengeopics.model.GTPhoto
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GoogleMapFragment : Fragment() {

    companion object {
        private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100
        private val PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 102
        private val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 104
    }

    private var _binding: FragmentGoogleMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: GoogleMapViewModel

    private var mMap: GoogleMap? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: LatLng? = null
    private var userMarker: Marker? = null
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val photoMarkers = ArrayList<Marker>()
    private lateinit var locationCallback: LocationCallback

    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        mMap?.setInfoWindowAdapter(MarkerInfoWindowAdapter(requireActivity()))
        mMap?.setOnMarkerClickListener { marker ->
            if (marker.tag == "user marker!") {
                findNavController().navigate(R.id.action_googleMapFragment_to_photoScrollingFragment)
                return@setOnMarkerClickListener true
            } else {
                return@setOnMarkerClickListener false
            }
        }
        // Add a marker in Sydney and move the camera
        val ucsb = LatLng(34.412936, -119.847863)
        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(ucsb,16f))

        getPermissions()
        updatePhotoMarkers()

        val route = viewModel.getRoute()
        lastKnownLocation = null
        for (i in 0 until route.size) {
            lastKnownLocation = if (lastKnownLocation == null) {
                route[i]
            } else {
                mMap?.addPolyline(PolylineOptions().add(lastKnownLocation).add(route[i]))
                Log.d("polyline","connecting ${lastKnownLocation!!.latitude},${lastKnownLocation!!.longitude} and ${route[i].latitude},${route[i].longitude}")
                route[i]
            }
        }

        startLocationUpdates()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        viewModel =
            ViewModelProvider(this)[GoogleMapViewModel::class.java]
        _binding = FragmentGoogleMapBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.controller = this
        binding.lifecycleOwner = this

        // update fab
        if (mAuth.currentUser != null) {
            binding.fabLogout.visibility = VISIBLE
            binding.fabLogout.setOnClickListener {
                logout()
            }

            binding.fab.setImageResource(R.drawable.ic_camera)
            binding.fab.setOnClickListener {
                findNavController().navigate(R.id.action_googleMapFragment_to_uploadPhotoFragment)
            }
        } else {
            binding.fabLogout.visibility = INVISIBLE
            binding.fab.setImageResource(R.drawable.ic_login)
            binding.fab.setOnClickListener {
                findNavController().navigate(R.id.action_googleMapFragment_to_loginFragment)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mAuth.currentUser == null) {
            (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.default_toolbar_title)
        } else {
            (activity as AppCompatActivity).supportActionBar?.title = mAuth.currentUser!!.email
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onPause() {
        super.onPause()

        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private fun logout() {
        mAuth.signOut()
        Toast.makeText(requireActivity(),getString(R.string.prompt_logout_success),Toast.LENGTH_SHORT)
            .show()
        binding.fabLogout.visibility = INVISIBLE
        binding.fab.setImageResource(R.drawable.ic_login)
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_googleMapFragment_to_loginFragment)
        }
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.default_toolbar_title)
        for (marker in photoMarkers) {
            marker.remove()
        }
        photoMarkers.clear()
    }

    // request permission if not granted
    private fun getPermissions() {

        if (ContextCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_DENIED
        ) {

            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }

        if (ContextCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {

            val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissions,
                PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )

        }

        if (ContextCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(
                requireActivity(),
                permissions,
                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 8_000
            fastestInterval = 5_000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    moveToLocation(location)
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun moveToLocation(location: Location) {
        Log.d("moveToLocation","${location.latitude}#${location.longitude}")
        val currentLatLng = LatLng(location.latitude, location.longitude)
        viewModel.addLocation(currentLatLng)

        // move circle
        if (userMarker == null) {
            userMarker = mMap?.addMarker(MarkerOptions().position(currentLatLng))
            userMarker?.tag = "user marker!"

        } else {
            userMarker!!.position = currentLatLng
        }
        // draw lines
        if (lastKnownLocation != null) {
            mMap?.addPolyline(PolylineOptions().add(lastKnownLocation).add(currentLatLng))
        }


        lastKnownLocation = currentLatLng
    }

    private fun updatePhotoMarkers() {
        if (mAuth.currentUser == null) {
            return
        }

        val userEmail = mAuth.currentUser!!.email
        val database = Firebase.database
        val mRef = database.getReference(getString(R.string.firebase_base_path))
        val userRef = mRef.child("$userEmail".replace('.','-'))
        userRef.get().addOnSuccessListener {  snapShot ->
            for (p in snapShot.children) {
                val gtphoto = p.getValue(GTPhoto::class.java)
                val position = LatLng(gtphoto!!.lat,gtphoto!!.lon)
                val marker = mMap?.addMarker(MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker
                    (BitmapDescriptorFactory.HUE_CYAN)).alpha(0.7f))
                marker?.tag = gtphoto.imagePath
                photoMarkers.add(marker!!)
            }

        }
    }

//    @SuppressLint("MissingPermission")
//    private fun updateLocationUI() {
//        if (mMap == null) {
//            return
//        }
//
//        try {
//            if (locationPermissionGranted) {
//                mMap.isMyLocationEnabled = true
//                mMap.uiSettings.isMyLocationButtonEnabled = true
//            } else {
//                mMap.isMyLocationEnabled = false
//                mMap.uiSettings.isMyLocationButtonEnabled = false
//            }
//        } catch (e: SecurityException) {
//            Log.e("Exception: %s", e.message, e)
//        }
//    }

//    @SuppressLint("MissingPermission")
//    private fun updateDeviceLocation() {
//        try {
//            if (locationPermissionGranted) {
//                val locationResult = fusedLocationProviderClient.lastLocation
//                locationResult.addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//                        lastKnownLocation = task.result
//                        if (lastKnownLocation != null) {
//                            mMap.addMarker()
//                        }
//                    }
//
//                }
//            }
//        } catch (e: SecurityException) {
//            Log.e("Exception: %s", e.message, e)
//        }
//    }
}