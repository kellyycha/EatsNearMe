package com.kellycha.eatsnearme.saved

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kellycha.eatsnearme.R
import com.kellycha.eatsnearme.details.DetailsFragment
import com.kellycha.eatsnearme.details.DetailsFragment.Companion.MAPVIEW_BUNDLE_KEY
import com.kellycha.eatsnearme.details.DetailsFragment.Companion.padding
import com.kellycha.eatsnearme.details.Restaurant
import com.kellycha.eatsnearme.restaurants.LocationService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_savedmap.*

class SavedMapFragment : Fragment(), OnMapReadyCallback {

    companion object{
        private const val KEY_RESTAURANT = "Restaurant"
        private const val TAG = "SavedFragment"

        fun newInstance(savedList: ArrayList<Restaurant>): SavedMapFragment {
            val fragment = SavedMapFragment()
            val args = Bundle()
            args.putParcelableArrayList(KEY_RESTAURANT, savedList)
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var savedList: ArrayList<Restaurant>

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
        savedList = args?.get(KEY_RESTAURANT) as ArrayList<Restaurant>

        initializeButtons()
        initGoogleMap(savedInstanceState)
    }

    private fun initializeButtons() {
        btnSaved.setOnClickListener{
            Log.i(TAG, "saved clicked")
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
            addRestaurants(map, builder, restaurant)
        }

        map.setOnInfoWindowClickListener {
            for (restaurant in savedList){
                val restaurantLocation = LatLng(restaurant.coordinates.latitude, restaurant.coordinates.longitude)
                if (restaurantLocation == it.position){
                    goToDetailsView(restaurant)
                }
            }
        }
        scaleMap(map, builder)
    }

    private fun addRestaurants(map: GoogleMap, builder: LatLngBounds.Builder, restaurant: Restaurant) {
        val restaurantLocation = LatLng(restaurant.coordinates.latitude, restaurant.coordinates.longitude)

        map.addMarker(
            MarkerOptions()
            .position(restaurantLocation)
            .title(restaurant.name))
        builder.include(restaurantLocation)

        savedMapView.onResume()

    }

    private fun goToDetailsView(restaurant: Restaurant) {
        val fragment = DetailsFragment.newInstance(restaurant)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_out_down, R.anim.slide_in_down)
        transaction.replace(R.id.flContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
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