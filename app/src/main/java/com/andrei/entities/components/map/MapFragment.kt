package com.andrei.entities.components.map

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.andrei.entities.R
import com.andrei.entities.core.TAG
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.map_fragment.*


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.v(TAG, "onCreateView")
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {

        Log.v(TAG, "onActivityCreated")
        super.onActivityCreated(savedInstanceState)

        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        mapView.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {

        googleMap.let {
            this.googleMap = it
            it.uiSettings.isMyLocationButtonEnabled = true
            it.setMinZoomPreference(15.0f)
            val ctx = context
            if (ctx != null && ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                setCameraOnCurrentLocation(it)
            }
        }
    }

    private fun setCameraOnCurrentLocation(it: GoogleMap) {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(parentFragment!!.activity as Activity)
        it.isMyLocationEnabled = true
        val loc = fusedLocationClient.lastLocation
        loc.addOnCompleteListener { location ->
            val currentLocation = location.result
            if (currentLocation != null)
                it.moveCamera(
                    CameraUpdateFactory.newLatLng(
                        LatLng(
                            currentLocation.latitude,
                            currentLocation.longitude
                        )
                    )
                )
        }
    }
}