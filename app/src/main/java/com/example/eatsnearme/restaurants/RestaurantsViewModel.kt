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
    private val restaurantAllDisplay = mutableListOf<YelpRestaurant>()
    private val restaurantDisplay = mutableListOf<YelpRestaurant>()
    private val restaurantNames = ArrayList<String>()
    private var polylineCoordinates = mutableListOf<LatLng>()
    private var spacedCoordinates = mutableListOf<LatLng>()

    private val _stateFlow = MutableStateFlow<RestaurantState>(RestaurantState.Loading)
    val stateFlow:StateFlow<RestaurantState> = _stateFlow

    private var currLocation = ""
    private var queryIndex = 0

    companion object {
        const val TAG = "RestaurantsViewModel"
        const val MIN_SIZE = 10
        const val defaultRadius = "1000"
        const val defaultPathRadius = "200"
    }

    private fun nextRestaurant(restaurant: YelpRestaurant, typeOfFood: String?, destination: String?, radius: String?) {

        // null check for if you start selecting restaurants without setting filters
        if (typeOfFood == null || destination == null){
            restaurantDisplay.remove(restaurant)
            setStateForNext()
            return
        }

        else if (radius.isNullOrEmpty()){
            nextRestaurant(restaurant, typeOfFood, destination, defaultPathRadius)
            return
        }

        restaurantDisplay.remove(restaurant)
        setStateForNext()


        if (destination.isNotEmpty()){
            queryAsNeeded(typeOfFood, radius.toInt())
        }

    }

    private fun setStateForNext(){
        if (restaurantDisplay.size > 0){
            _stateFlow.tryEmit(RestaurantState.Success(restaurantDisplay[0]))
        }
        else{
            _stateFlow.value = RestaurantState.Idle

        }
    }

    private fun fetchRestaurantsForCurrentLocation(typeOfFood: String, destination: String, radius: Int) {
        LocationService().getLastLocation(getApplication(), object : LocationService.MyLocationListener{
            override fun onLocationChanged(location: Location) {
                Log.d(TAG,"Coordinates: ${location.latitude}, ${location.longitude}")
                currLocation = "${location.latitude}, ${location.longitude}"
                if (destination.isBlank()){
                    queryYelp(typeOfFood, currLocation, radius)
                }
                else{
                    getPath(typeOfFood, currLocation, destination, radius)
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

    fun fetchRestaurants(typeOfFood: String = "", location: String = "", destination: String = "", radius: String = "") {
        Log.i(TAG, "type of food: $typeOfFood")
        Log.i(TAG, "Location: $location")
        Log.i(TAG, "destination: $destination")
        Log.i(TAG, "radius: $radius")

        restaurantResults.clear()
        restaurantDisplay.clear()
        restaurantNames.clear()
        _stateFlow.value = RestaurantState.Loading

        if (radius.isBlank()){
            fetchRestaurants(typeOfFood, location, destination, if (destination.isBlank()) defaultRadius else defaultPathRadius)
            return
        }

        if(location.isBlank() && LocationService().hasPermissions(getApplication())){
            Log.i(TAG, "no location")
            fetchRestaurantsForCurrentLocation(typeOfFood, destination, radius.toInt())
        }
        else if (location.isNotBlank() && destination.isBlank()){
            queryYelp(typeOfFood, location, radius.toInt())
        }
        else if (location.isNotBlank() && destination.isNotBlank()){
            getPath(typeOfFood, location, destination, radius.toInt())
        }
        else{
            _stateFlow.value = RestaurantState.Idle
        }

    }

    private fun getPath(typeOfFood: String, origin: String, destination: String, radius: Int) {
        Log.i("MAPS", "getting path")
        Log.i(TAG, "type of food: $typeOfFood")
        Log.i(TAG, "origin: $origin")
        Log.i(TAG, "destination: $destination")
        Log.i(TAG, "radius: $radius")

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

        for (coord in polylineCoordinates) {
            Log.i("MAPS", "all: ${coord.latitude},${coord.longitude}")
        }

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

        if (spacedCoordinates.size == 0 && polylineCoordinates.size != 0){
            spacedCoordinates.add(polylineCoordinates[index1])
        }

        Log.i("MAPS","amt in spaced: ${spacedCoordinates.size}")

        for (coord in spacedCoordinates) {
            Log.i("MAPS", "spaced: ${coord.latitude},${coord.longitude}")
        }

        queryIndex = 0
        queryAsNeeded(typeOfFood, radius)

    }

    private fun queryAsNeeded(typeOfFood: String, radius: Int){
        Log.i("QUERY","query as needed")
        Log.i("QUERY","list size: ${restaurantDisplay.size}")
        if (restaurantDisplay.size < MIN_SIZE && queryIndex < spacedCoordinates.size){
            val coordinate = spacedCoordinates[queryIndex]
            val location = "${coordinate.latitude},${coordinate.longitude}"
            Log.i("QUERY","query yelp at: $location")
            Log.i("QUERY","query index: $queryIndex")
            queryYelp(typeOfFood, location, radius)
            queryIndex++
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
                            && restaurant !in restaurantAllDisplay
                            && restaurant.location.address.isNotEmpty()
                            && restaurant.image_url.isNotEmpty()){
                            restaurantAllDisplay.add(restaurant)
                            restaurantDisplay.add(restaurant)
                        }
                    }

                    Log.i(TAG, "all restaurants: $restaurantResults")
                    Log.i(TAG, "display restaurants: $restaurantDisplay")

                    for (rest in restaurantDisplay){
                        Log.i("MAPS", "${rest.name}, ${rest.location.address}")
                        restaurantNames.add(rest.name)
                    }

                    if (restaurantDisplay.isEmpty()){
                        _stateFlow.value = RestaurantState.Idle
                    }
                    else{
                        _stateFlow.value = RestaurantState.Success(restaurantDisplay[0])
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
            return true
        }
        return false
    }

    fun storeRestaurant(restaurant: YelpRestaurant, isSaved: Boolean, typeOfFood: String? = "", destination: String? = "", radius: String? = "") {
        val currentUser = ParseUser.getCurrentUser()
        val saved = SavedRestaurants()
        saved.setIsSaved(isSaved)
        saved.setUser(currentUser)
        saved.setRestaurantName(restaurant.name)
        //TODO: crashes if restaurant price is not available (null), even though price column is not required in parse
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
            }
            Log.i(TAG, "Restaurant save was successful")
        })

        nextRestaurant(restaurant, typeOfFood, destination, radius)
    }

    fun getRestaurantsToDisplay(): MutableList<YelpRestaurant> {
        return restaurantDisplay
    }


    sealed class RestaurantState {
        object Loading : RestaurantState()
        object Idle : RestaurantState()
        class Success(var restaurant : YelpRestaurant) : RestaurantState()
    }

}

