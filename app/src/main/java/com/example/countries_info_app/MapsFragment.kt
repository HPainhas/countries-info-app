package com.example.countries_info_app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var location: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapsFragmentContainer) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true // Enable zoom controls (the + and - buttons)
        map.uiSettings.isZoomGesturesEnabled = true // Enable zoom gestures (pinch to zoom, double tap to zoom)

        location?.let {
            updateMapLocation(it)
        }

        // Add a marker at the given coordinates and move the camera
        //val location = LatLng(42.5, 1.52)
        //map.addMarker(MarkerOptions().position(location).title("Marker"))
        //map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 5f))
    }

    fun updateLocation(lat: Double, lng: Double) {
        location = LatLng(lat, lng)
        updateMapLocation(location!!)
    }

    private fun updateMapLocation(location: LatLng) {
        map.clear()
        map.addMarker(MarkerOptions().position(location).title("Marker in Location"))

        // Add marker for the new location
        val marker = map.addMarker(MarkerOptions().position(location).title("Marker in Location"))

        // Define camera update to zoom out slowly from the current marker
        val zoomOutCameraUpdate = marker?.let { CameraUpdateFactory.newLatLngZoom(it.position, map.cameraPosition.zoom - 2f) }

        // Define camera update to "fly" to the new marker
        val flyToNewMarkerCameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 5f) // Adjust the zoom level as needed

        // Execute the zoom out animation
        if (zoomOutCameraUpdate != null) {
            map.animateCamera(zoomOutCameraUpdate, 3000, object : GoogleMap.CancelableCallback {
                override fun onFinish() {
                    // After zoom out animation finishes, execute the fly to new marker animation
                    map.animateCamera(flyToNewMarkerCameraUpdate, 3000, null)
                }

                override fun onCancel() {
                    // Handle cancellation if needed
                }
            })
        }
    }
}
