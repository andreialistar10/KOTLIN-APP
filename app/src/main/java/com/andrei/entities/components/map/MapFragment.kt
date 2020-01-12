package com.andrei.entities.components.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.andrei.entities.R
import com.andrei.entities.core.TAG
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.map_fragment.*

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

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
            val dhaka = LatLng(23.777176, 90.399452)
            it.addMarker(MarkerOptions().position(dhaka).title("Mergee baaa"))
            it.moveCamera(CameraUpdateFactory.newLatLng(dhaka))
        }
    }
}