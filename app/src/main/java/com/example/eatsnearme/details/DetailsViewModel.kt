package com.example.eatsnearme.details

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.eatsnearme.googleMaps.DirectionsResponse
import com.example.eatsnearme.googleMaps.MAPS_API_KEY
import com.example.eatsnearme.googleMaps.MapsService
import com.example.eatsnearme.restaurants.LocationService
import com.example.eatsnearme.restaurants.RestaurantsViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.system.exitProcess

class DetailsViewModel(application: Application) : AndroidViewModel(application) {

    companion object{
        const val TAG = "DetailsFragment"
    }

    private val _currLocation = MutableStateFlow<CurrLocationState>(CurrLocationState.Loading)
    val currLocation: StateFlow<CurrLocationState> = _currLocation

    private val _path = MutableStateFlow<PathState>(PathState.Loading)
    val path: StateFlow<PathState> = _path

    private var polylineCoordinates = mutableListOf<LatLng>()

    fun getCurrentLocation() {
        _currLocation.value = CurrLocationState.Loading
        LocationService().getLastLocation(getApplication(), object : LocationService.MyLocationListener{
            override fun onLocationChanged(location: Location) {
                var currLocation = LatLng(location.latitude, location.longitude)
                _currLocation.value = CurrLocationState.Loaded(currLocation)
            }
        })
    }

    fun getPath(origin: String, destination: String): MutableList<LatLng> {
        Log.i(TAG, "getting path")
        _path.value = PathState.Loading
        polylineCoordinates.clear()
        val mapsService = MapsService.create()
        mapsService.searchPath(MAPS_API_KEY, origin, destination, "WALKING")
            .enqueue(object : Callback<DirectionsResponse> {
                override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                    Log.i(TAG, "onResponse $response")
                    val body = response.body()
                    if (body == null) {
                        Log.w(TAG, "did not receive valid response body from Maps API")
                        return
                    }
                    Log.i(TAG, "geocoded waypoints results: ${body.geocoded_waypoints}")
                    Log.i(TAG, "routes results: ${body.routes}")

                    polylineCoordinates = PolyUtil.decode(body.routes.component1().overview_polyline.points)
                    Log.i(TAG, "overview polyline results: $polylineCoordinates")

                    _path.value = PathState.Loaded(polylineCoordinates)
                }

                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    Log.i(TAG, "onFailure $t")
                }
            })
        return polylineCoordinates
    }

    sealed class CurrLocationState {
        object Loading : CurrLocationState()
        data class Loaded(var coordinates : LatLng): CurrLocationState()
    }

    sealed class PathState {
        object Loading : PathState()
        data class Loaded(var points : MutableList<LatLng>): PathState()
    }

}