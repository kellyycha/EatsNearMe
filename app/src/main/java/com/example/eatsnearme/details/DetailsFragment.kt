package com.example.eatsnearme.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet.GONE
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.eatsnearme.R
import com.example.eatsnearme.restaurants.LocationService
import com.example.eatsnearme.restaurants.RestaurantsViewModel
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
        const val padding = 200
    }
    private val viewModel: DetailsViewModel by viewModels()
    private lateinit var inputRestaurant : Restaurant

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // get parcel and extract data
        val args = this.arguments
        inputRestaurant = args?.get("Restaurant") as Restaurant

        btnExitDetail.setOnClickListener{
            requireActivity().onBackPressed()
        }

        setRestaurantInfo()

        initGoogleMap(savedInstanceState)
    }

    private fun setRestaurantInfo() {
        tvClickedName.text = inputRestaurant.name
        tvClickedPrice.text = inputRestaurant.price
        clickedRatingBar.rating = inputRestaurant.rating.toFloat()
        tvClickedReviewCount.text = inputRestaurant.review_count.toString()
        tvClickedAddress.text = inputRestaurant.address
        tvClickedPhone.text = inputRestaurant.phone

        tvOpened.visibility = GONE
        // TODO: Figure out why this is always false
//        if (viewModel.getCurrentRestaurant().is_open_now){
//            tvOpened.text = "Open Now"
//            tvOpened.setTextColor(Color.parseColor("#32a832"))
//        }
//        else{
//            tvOpened.text = "Closed Now"
//            tvOpened.setTextColor(Color.parseColor("#e30707"))
//        }
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

        val restaurantLocation = LatLng(inputRestaurant.coordinates.latitude.toDouble(),
            inputRestaurant.coordinates.longitude.toDouble())

        map.addMarker(MarkerOptions()
            .position(restaurantLocation)
            .title(inputRestaurant.name))
        builder.include(restaurantLocation)

        // TODO: get origin and dest from view model
//        map.addMarker(MarkerOptions()
//            .position(viewModel.mapOrigin)
//            .title(viewModel.location)
//            .icon(BitmapDescriptorFactory.defaultMarker(light_blue)))
//
//        builder.include(viewModel.mapOrigin)
//
//        viewModel.mapDestination?.let { mapDestination ->
//            map.addMarker(MarkerOptions()
//                .position(mapDestination)
//                .title(viewModel.destination)
//                .icon(BitmapDescriptorFactory.defaultMarker(light_blue)))
//            builder.include(mapDestination)
//        }


        if (LocationService().hasPermissions(requireContext())){
            map.isMyLocationEnabled = true
        }

        val bounds = builder.build()
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
            mapView.onDestroy()
        }
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}