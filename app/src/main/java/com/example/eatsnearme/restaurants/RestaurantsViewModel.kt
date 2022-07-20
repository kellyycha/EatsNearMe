package com.example.eatsnearme.restaurants

import android.app.Application
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.eatsnearme.SavedRestaurants
import com.example.eatsnearme.googleMaps.DirectionsResponse
import com.example.eatsnearme.googleMaps.MAPS_API_KEY
import com.example.eatsnearme.googleMaps.MapsService
import com.example.eatsnearme.saved.SavedFragment
import com.example.eatsnearme.yelp.YELP_API_KEY
import com.example.eatsnearme.yelp.YelpRestaurant
import com.example.eatsnearme.yelp.YelpSearchResult
import com.example.eatsnearme.yelp.YelpService
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.parse.ParseUser
import com.parse.SaveCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RestaurantsViewModel(application: Application) : AndroidViewModel(application) {

    private val restaurantResults = mutableListOf<YelpRestaurant>()
    private val restaurantDisplay = mutableListOf<YelpRestaurant>()
    private var polylineCoordinates = mutableListOf<LatLng>()
    private var spacedCoordinates = mutableListOf<LatLng>()

    private val _stateFlow = MutableStateFlow<RestaurantState>(RestaurantState.Loading)
    val stateFlow:StateFlow<RestaurantState> = _stateFlow

    private var index = 0
    private var currLocation = ""

    companion object {
        const val TAG = "RestaurantsViewModel"
        const val defaultRadius = "1000"
    }

    private fun nextRestaurant() {
        if (restaurantDisplay.size > index+1){
            _stateFlow.tryEmit(RestaurantState.Success(restaurantDisplay[index]))
        }
        else{
            _stateFlow.value = RestaurantState.Idle
            Toast.makeText(getApplication(), "No more restaurants to show", Toast.LENGTH_SHORT).show()
        }

    }

    private fun fetchRestaurantsForCurrentLocation(typeOfFood: String, destination: String, radius: Int) {
        LocationService().getLastLocation(getApplication(), object : LocationService.MyLocationListener{
            override fun onLocationChanged(location: Location) {
                Log.d(TAG,"Coordinates: ${location.latitude}, ${location.longitude}")
                currLocation = "${location.latitude}, ${location.longitude}"
                if (destination.isEmpty()){
                    queryYelp(typeOfFood, currLocation, radius)
                }
                else{
                    getPath(typeOfFood, currLocation, destination, radius.toInt())
                }

            }
        })

    }

    init {
        Log.i(TAG, "init")
        // TODO: load saved restaurants before fetching.
        //  right now it only filters after clicking saved tab and going back and then clicking search again
        fetchRestaurants()
    }

    fun fetchRestaurants(typeOfFood: String = "", location: String = "", destination: String = "", radius: String = defaultRadius) {
        Log.i(TAG, "type of food: $typeOfFood")
        Log.i(TAG, "Location: $location")
        Log.i(TAG, "radius: $radius")

        index = 0
        restaurantResults.clear()
        restaurantDisplay.clear()
        _stateFlow.value = RestaurantState.Loading

        if (radius.isEmpty()){
            fetchRestaurants(typeOfFood, location, destination, defaultRadius)
            return
        }

        if(location.isEmpty() && LocationService().hasPermissions(getApplication())){
            Log.i(TAG, "no location")
            fetchRestaurantsForCurrentLocation(typeOfFood, destination, radius.toInt())
        }
        else if (location.isNotEmpty() && destination.isEmpty()){
            queryYelp(typeOfFood, location, radius.toInt())
        }
        else if (location.isNotEmpty() && destination.isNotEmpty()){
            getPath(typeOfFood, location, destination, radius.toInt())
        }
        else{
            _stateFlow.value = RestaurantState.Idle
        }

    }

    private fun getPath(typeOfFood: String, origin: String, destination: String, radius: Int) {
        Log.i("MAPS", "getting path")
        polylineCoordinates.clear()
        val mapsService = MapsService.create()
        mapsService.searchPath(MAPS_API_KEY, origin, destination, "WALKING")
            .enqueue(object : Callback<DirectionsResponse> {
                override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                    Log.i("MAPS", "onResponse $response")
                    val body = response.body()
                    if (body == null) {
                        Log.w("MAPS", "did not receive valid response body from Maps API")
                        return
                    }
                    Log.i("MAPS", "geocoded waypoints results: ${body.geocoded_waypoints}")
                    Log.i("MAPS", "routes results: ${body.routes}")

                    polylineCoordinates = PolyUtil.decode(body.routes.component1().overview_polyline.points)
                    Log.i("MAPS", "overview polyline results: $polylineCoordinates")


                    getSpacedPoints(polylineCoordinates, typeOfFood, radius)

                }

                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    Log.i("MAPS", "onFailure $t")
                }
            })
    }

    private fun getSpacedPoints(polylineCoordinates: MutableList<LatLng>, typeOfFood: String, radius: Int) {
        spacedCoordinates.clear()
        Log.i("MAPS","amt in all: ${polylineCoordinates.size}")

        var index1 = 0
        var index2 = 1

        while(index2 < polylineCoordinates.size){
            var start = polylineCoordinates[index1]
            var next = polylineCoordinates[index2]

            val results = FloatArray(1)
            Location.distanceBetween(start.latitude, start.longitude, next.latitude, next.longitude, results)

            if (results[0] > radius){
                spacedCoordinates.add(next)
                index1 = index2
                index2++
            }
            else{
                index2++
            }
        }

        Log.i("MAPS","amt in spaced: ${spacedCoordinates.size}")

        for (coord in spacedCoordinates) {
            Log.i("MAPS", "spaced: ${coord.latitude},${coord.longitude}")
            val location = "${coord.latitude},${coord.longitude}"
            queryYelp(typeOfFood, location, radius)
        }

    }


    private fun queryYelp(typeOfFood: String, location: String, radius: Int){
        Log.i(TAG, "query yelp")
        val yelpService = YelpService.create()

        yelpService.searchRestaurants("Bearer $YELP_API_KEY", typeOfFood, location, radius)
            .enqueue(object : Callback<YelpSearchResult> {
                override fun onResponse(call: Call<YelpSearchResult>, response: Response<YelpSearchResult>) {
                    Log.i(TAG, "onResponse $response")
                    val body = response.body()
                    if (body == null) {
                        Log.w(TAG, "did not receive valid response body from Yelp API")
                        return
                    }

                    Log.i(TAG, "Adding all restaurants...")
                    restaurantResults.addAll(body.restaurants)

                    for (restaurant in restaurantResults){
                        if (!isSkipped(restaurant) && !isSaved(restaurant)
                            && restaurant !in restaurantDisplay && restaurant.location.address.isNotEmpty()){
                            restaurantDisplay.add(restaurant)
                        }
                    }

                    Log.i(TAG, "all restaurants: $restaurantResults")
                    Log.i(TAG, "display restaurants: $restaurantDisplay")

                    for (rest in restaurantDisplay){
                        Log.i("MAPS", "${rest.name}, ${rest.location.address}")
                    }

                    if (restaurantDisplay.isEmpty()){
                        _stateFlow.value = RestaurantState.Idle
                        Toast.makeText(getApplication(), "No restaurants within radius", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        _stateFlow.value = RestaurantState.Success(restaurantDisplay[index])
                    }
                }

                override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                    Log.i(TAG, "onFailure $t")
                }

            })

    }

    private fun isSaved(restaurant: YelpRestaurant): Boolean{
        if (restaurant.name in SavedFragment().getSavedList()){
            return true
        }
        return false
    }

    private fun isSkipped(restaurant: YelpRestaurant): Boolean{
        if (restaurant.name in SavedFragment().getSkippedList()){
//            skippedIndex++
//            restaurantSkipped.add(restaurant)
            return true
        }
        return false
    }

    fun storeRestaurant(restaurant: YelpRestaurant, isSaved: Boolean) {
        val currentUser = ParseUser.getCurrentUser()
        val saved = SavedRestaurants()
        saved.setIsSaved(isSaved)
        saved.setUser(currentUser)
        saved.setRestaurantName(restaurant.name)
        //saved.setRestaurantPrice(restaurant.price)
        saved.setRestaurantRating(restaurant.rating)
        saved.setRestaurantImage(restaurant.image_url)
        saved.setRestaurantReviewCount(restaurant.review_count)
        saved.setRestaurantAddress(restaurant.location.address)
        var categories = ""
        for (i in restaurant.categories.indices){
            categories += restaurant.categories[i].title+", "
        }
        saved.setRestaurantCategories(categories.dropLast(2))

        saved.saveInBackground(SaveCallback { e ->
            if (e != null) {
                Log.e(TAG, "Error while saving", e)
                Toast.makeText(getApplication(), "Error while saving", Toast.LENGTH_SHORT).show()
            }
            Log.i(TAG, "Restaurant save was successful")
        })

        restaurantDisplay.remove(restaurant)
        nextRestaurant()
    }


    sealed class RestaurantState {
        object Loading : RestaurantState()
        object Idle : RestaurantState()
        class Success(var restaurant : YelpRestaurant) : RestaurantState()
    }

}

