package com.kellycha.eatsnearme.details

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.kellycha.eatsnearme.R
import com.kellycha.eatsnearme.restaurants.LocationService
import com.kellycha.eatsnearme.restaurants.RestaurantsViewModel
import com.kellycha.eatsnearme.restaurants.collectLatestLifecycleFlow
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.fragment_details.*

class DetailsFragment : Fragment(), OnMapReadyCallback {

    companion object{
        const val MAPVIEW_BUNDLE_KEY = "MapViewBundleKey"
        private const val TAG = "DetailsFragment"
        private const val light_blue = 200F
        const val padding = 200
        private const val KEY_RESTAURANT = "Restaurant"

        fun newInstance(restaurant: Restaurant): DetailsFragment {
            val fragment = DetailsFragment()
            val args = Bundle()
            args.putParcelable(KEY_RESTAURANT, restaurant)
            fragment.arguments = args
            return fragment
        }
    }
    private val viewModel: DetailsViewModel by activityViewModels()
    private val restaurantsVM: RestaurantsViewModel by activityViewModels()
    private var polyline: Polyline? = null

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

        val args = this.arguments
        inputRestaurant = args?.get(KEY_RESTAURANT) as Restaurant

        initializeButtons()
        setRestaurantInfo()
        initGoogleMap(savedInstanceState)
    }

    private fun initializeButtons() {
        btnShareYelpUrl.setOnClickListener{
            setClipboard(requireContext(), inputRestaurant)
        }

        btnExitDetail.setOnClickListener{
            requireActivity().onBackPressed()
        }
    }

    private fun setClipboard(context: Context, restaurant: Restaurant) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Copied Text", restaurant.url)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(requireContext(), "Link to ${restaurant.name} copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun setRestaurantInfo() {
        tvClickedName.text = inputRestaurant.name
        if(inputRestaurant.price != "null"){
            tvClickedPrice.text = inputRestaurant.price
        }
        clickedRatingBar.rating = inputRestaurant.rating.toFloat()
        tvClickedReviewCount.text = inputRestaurant.review_count.toString()
        tvClickedAddress.text = inputRestaurant.address
        tvClickedPhone.text = inputRestaurant.phone

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

        if (LocationService().hasPermissions(requireContext())){
            map.isMyLocationEnabled = true
        }

        val builder = LatLngBounds.Builder()

        addRestaurantLocation(map, builder)

        if (!inputRestaurant.is_saved){
            addOriginDestinationMarkers(map, builder)
        }
        else if (inputRestaurant.is_saved && LocationService().hasPermissions(requireContext())){
            addCurrentLocationMarker(map, builder)
        }
    }

    private fun addRestaurantLocation(map: GoogleMap, builder: LatLngBounds.Builder) {
        val restaurantLocation = inputRestaurant.coordinates
        map.addMarker(MarkerOptions()
            .position(restaurantLocation)
            .title(inputRestaurant.name))
        builder.include(restaurantLocation)
    }

    private fun addCurrentLocationMarker(map: GoogleMap, builder: LatLngBounds.Builder) {
        viewModel.getCurrentLocation()

        collectLatestLifecycleFlow(viewModel.currLocation) { curr ->
            when (curr) {
                is DetailsViewModel.CurrLocationState.Loading -> {
                    Log.i(TAG, "Loading location")
                }
                is DetailsViewModel.CurrLocationState.Loaded -> {
                    Log.i(TAG, "Loaded location")

                    map.addMarker(
                        MarkerOptions()
                            .position(curr.coordinates)
                            .title("Current Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(light_blue))
                    )
                    builder.include(curr.coordinates)
                    scaleMap(map, builder)
                }
            }
        }
    }

    private fun addPolyline(directionPoints: MutableList<LatLng>, map: GoogleMap) {
        polyline = map.addPolyline(
            PolylineOptions()
                .color(Color.BLUE)
                .addAll(directionPoints))
    }

    private fun addOriginDestinationMarkers(map: GoogleMap, builder: LatLngBounds.Builder) {
        val origin = restaurantsVM.mapOrigin
        map.addMarker(MarkerOptions()
            .position(origin)
            .title(restaurantsVM.location)
            .icon(BitmapDescriptorFactory.defaultMarker(light_blue)))
        builder.include(origin)

        restaurantsVM.mapDestination?.let { destination ->
            map.addMarker(MarkerOptions()
                .position(destination)
                .title(restaurantsVM.destination)
                .icon(BitmapDescriptorFactory.defaultMarker(light_blue)))
            builder.include(destination)

            addPolyline(restaurantsVM.getPath(), map)
        }

        scaleMap(map, builder)
    }

    private fun scaleMap(map: GoogleMap, builder: LatLngBounds.Builder) {
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