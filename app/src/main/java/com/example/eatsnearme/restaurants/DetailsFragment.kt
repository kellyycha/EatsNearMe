package com.example.eatsnearme.restaurants

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.eatsnearme.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_details.*


class DetailsFragment : Fragment(), OnMapReadyCallback {

    companion object{
        const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
        const val TAG = "DetailsFragment"
        const val light_blue = 200F
    }
    private val viewModel: RestaurantsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvClickedName.text = viewModel.getCurrentRestaurant().name

        initGoogleMap(savedInstanceState)
    }

    private fun initGoogleMap(savedInstanceState: Bundle?) {
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }

        mapView.onCreate(mapViewBundle)

        mapView.getMapAsync(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onMapReady(map: GoogleMap) {
        map.clear()

        val builder = LatLngBounds.Builder()

        val restaurantLocation = LatLng(viewModel.getCurrentRestaurant().coordinates.latitude.toDouble(),
            viewModel.getCurrentRestaurant().coordinates.longitude.toDouble())

        map.addMarker(MarkerOptions()
            .position(restaurantLocation)
            .title(viewModel.getCurrentRestaurant().name))
        builder.include(restaurantLocation)

        map.addMarker(MarkerOptions()
            .position(viewModel.mapOrigin!!)
            .title(viewModel.location)
            .icon(BitmapDescriptorFactory.defaultMarker(light_blue)))

        builder.include(viewModel.mapOrigin!!)

        if (viewModel.mapDestination != null){
            map.addMarker(MarkerOptions()
                .position(viewModel.mapDestination!!)
                .title(viewModel.destination)
                .icon(BitmapDescriptorFactory.defaultMarker(light_blue)))
            builder.include(viewModel.mapDestination!!)
        }

        if (LocationService().hasPermissions(requireContext())){
            map.isMyLocationEnabled = true
        }

        val bounds = builder.build()
        val padding = 200
        val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        map.moveCamera(cu)

    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onPause() {
        mapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        if(mapView != null) {
            mapView.onDestroy();
        }
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}