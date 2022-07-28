package com.example.eatsnearme.saved

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.eatsnearme.R
import com.example.eatsnearme.parse.SavedRestaurants
import com.example.eatsnearme.restaurants.LocationService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_savedmap.*

class SavedMapFragment : Fragment(), OnMapReadyCallback {

    companion object{
        const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
        const val TAG = "SavedFragment"
        const val padding = 200
    }

    private lateinit var savedList: ArrayList<SavedRestaurant>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_savedmap, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = this.arguments
        savedList = args?.get("saved") as ArrayList<SavedRestaurant>

        initializeButtons()
        initGoogleMap(savedInstanceState)
    }

    private fun initializeButtons() {
        btnSaved.setOnClickListener{
            Log.i("button", "saved clicked")
            requireActivity().onBackPressed()
        }
    }


    private fun initGoogleMap(savedInstanceState: Bundle?) {
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }

        savedMapView.onCreate(mapViewBundle)

        savedMapView.getMapAsync(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        savedMapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onMapReady(map: GoogleMap) {
        map.clear()

        if (LocationService().hasPermissions(requireContext())){
            map.isMyLocationEnabled = true
        }
        val builder = LatLngBounds.Builder()

        for (restaurant in savedList){
            Log.i("map", "restaurants ${restaurant.name}")
            addRestaurants(map, builder, restaurant)
        }
        scaleMap(map, builder)

    }

    private fun addRestaurants(map: GoogleMap, builder: LatLngBounds.Builder, restaurant: SavedRestaurant) {
        val restaurantLocation = LatLng(restaurant.latitude, restaurant.longitude)
        map.addMarker(
            MarkerOptions()
            .position(restaurantLocation)
            .title(restaurant.name))
        builder.include(restaurantLocation)
    }

    private fun scaleMap(map: GoogleMap, builder: LatLngBounds.Builder) {
        val bounds = builder.build()
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        map.moveCamera(cu)
    }

    override fun onResume() {
        super.onResume()
        savedMapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        savedMapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        savedMapView.onStop()
    }

    override fun onPause() {
        savedMapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        if(savedMapView != null) {
            savedMapView.onDestroy()
        }
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        savedMapView.onLowMemory()
    }

}