package com.kellycha.eatsnearme.restaurants

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.kellycha.eatsnearme.parse.SavedRestaurants
import com.kellycha.eatsnearme.details.Restaurant.Companion.categoriesToString
import com.kellycha.eatsnearme.googleMaps.DirectionsResponse
import com.kellycha.eatsnearme.googleMaps.MAPS_API_KEY
import com.kellycha.eatsnearme.googleMaps.MapsService
import com.kellycha.eatsnearme.saved.SavedViewModel
import com.kellycha.eatsnearme.yelp.YELP_API_KEY
import com.kellycha.eatsnearme.yelp.YelpRestaurant
import com.kellycha.eatsnearme.yelp.YelpSearchResult
import com.kellycha.eatsnearme.yelp.YelpService
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.parse.ParseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestaurantsViewModel(application: Application) : AndroidViewModel(application) {

    private val restaurantResults = mutableListOf<YelpRestaurant>()
    private val restaurantAllDisplay = mutableListOf<YelpRestaurant>()
    private val restaurantDisplay = mutableListOf<YelpRestaurant>()
    private var polylineCoordinates = mutableListOf<LatLng>()
    private var spacedCoordinates = mutableListOf<LatLng>()

    private val _stateFlow = MutableStateFlow<RestaurantState>(RestaurantState.Loading)
    val stateFlow:StateFlow<RestaurantState> = _stateFlow

    private var currLocation = ""
    private var queryIndex = 0

    var typeOfFood : String? = null
    var location : String? = null
    var destination : String? = null
    var radius : String? = null
    var prices : String = "1,2,3,4"
    var openNow : Boolean = false

    lateinit var mapOrigin: LatLng
    var mapDestination: LatLng? = null

    private val savedVM = SavedViewModel()

    companion object {
        private const val TAG = "RestaurantsViewModel"
        private const val MIN_SIZE = 10
        private const val defaultRadius = "1000"
        private const val defaultPathRadius = "100"
        private const val MAX_RESPONSES = 50
        private val KEY_WALKING = MapsService.TravelMode.WALKING
    }

    init {
        fetchRestaurants()
    }

    fun fetchRestaurants(typeOfFood: String = "",
                         location: String = "",
                         destination: String = "",
                         radius: String = "",
                         prices: String = "1,2,3,4",
                         openNow: Boolean = false) {

        Log.i(TAG, "type of food: $typeOfFood")
        Log.i(TAG, "Location: $location")
        Log.i(TAG, "destination: $destination")
        Log.i(TAG, "radius: $radius")
        Log.i(TAG, "prices: $prices")
        Log.i(TAG, "openNow: $openNow")

        savedVM.querySaved()

        restaurantResults.clear()
        restaurantDisplay.clear()
        restaurantAllDisplay.clear()
        _stateFlow.value = RestaurantState.Loading

        mapDestination = null

        if (radius.isBlank()){
            fetchRestaurants(typeOfFood, location, destination, if (destination.isBlank()) defaultRadius else defaultPathRadius, prices, openNow)
            return
        }

        if(location.isBlank() && LocationService().hasPermissions(getApplication())){
            fetchRestaurantsForCurrentLocation(typeOfFood, destination, radius.toInt(), prices, openNow)
        }
        else if (location.isNotBlank() && destination.isBlank()){
            setOriginLatLng(location)
            queryYelp(typeOfFood, location, radius.toInt(), prices, openNow)
        }
        else if (location.isNotBlank() && destination.isNotBlank()){
            setOriginLatLng(location)
            getPath(typeOfFood, location, destination, radius.toInt(), prices, openNow)
        }
        else{
            _stateFlow.value = RestaurantState.Idle
        }
    }

    private fun fetchRestaurantsForCurrentLocation(typeOfFood: String, destination: String, radius: Int, prices: String, openNow: Boolean) {
        LocationService().getLastLocation(getApplication(), object :
            LocationService.MyLocationListener {
            override fun onLocationChanged(currentLocation: Location) {
                currLocation = "${currentLocation.latitude}, ${currentLocation.longitude}"
                mapOrigin = LatLng(currentLocation.latitude, currentLocation.longitude)
                location = "Current Location"
                if (destination.isBlank()){
                    queryYelp(typeOfFood, currLocation, radius, prices, openNow)
                }
                else{
                    getPath(typeOfFood, currLocation, destination, radius, prices, openNow)
                }
            }
        })
    }

    private fun setOriginLatLng(location: String) {
        val mapsService = MapsService.create()
        mapsService.searchPath(MAPS_API_KEY, location, location, KEY_WALKING)
            .enqueue(object : Callback<DirectionsResponse> {
                override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                    val body = response.body() ?: return

                    mapOrigin = LatLng(
                        body.routes.component1().legs.component1().end_location.lat.toDouble(),
                        body.routes.component1().legs.component1().end_location.lng.toDouble())
                }

                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    Log.i("MAPS", "onFailure $t")
                }
            })
    }

    private fun getPath(typeOfFood: String, origin: String, destination: String, radius: Int, prices: String, openNow: Boolean) {
        polylineCoordinates.clear()
        val mapsService = MapsService.create()
        mapsService.searchPath(MAPS_API_KEY, origin, destination, KEY_WALKING)
            .enqueue(object : Callback<DirectionsResponse> {
                override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                    Log.i("MAPS", "onResponse $response")
                    val body = response.body()
                    if (body == null) {
                        Log.w("MAPS", "did not receive valid response body from Maps API")
                        return
                    }

                    polylineCoordinates = PolyUtil.decode(body.routes.component1().overview_polyline.points)

                    mapDestination = LatLng(
                        body.routes.component1().legs.component1().end_location.lat.toDouble(),
                        body.routes.component1().legs.component1().end_location.lng.toDouble())

                    getSpacedPoints(polylineCoordinates, typeOfFood, radius, prices, openNow)

                }

                override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {
                    Log.i("MAPS", "onFailure $t")
                }
            })
    }

    private fun getSpacedPoints(polylineCoordinates: MutableList<LatLng>, typeOfFood: String, radius: Int, prices: String, openNow: Boolean) {
        spacedCoordinates.clear()

        var index1 = 0
        var index2 = 1

        while(index2 < polylineCoordinates.size){
            var start = polylineCoordinates[index1]
            var next = polylineCoordinates[index2]

            val results = FloatArray(1)
            Location.distanceBetween(start.latitude, start.longitude, next.latitude, next.longitude, results)

            if (results.component1() > radius){
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

        queryIndex = 0
        queryAsNeeded(typeOfFood, radius, prices, openNow)

    }

    private fun queryAsNeeded(typeOfFood: String, radius: Int, prices: String, openNow: Boolean){
        Log.i("QUERY","list size: ${restaurantDisplay.size}")

        if (restaurantDisplay.size < MIN_SIZE && queryIndex < spacedCoordinates.size){
            val coordinate = spacedCoordinates[queryIndex]
            val location = "${coordinate.latitude},${coordinate.longitude}"
            Log.i("QUERY","query index: $queryIndex")
            queryYelp(typeOfFood, location, radius, prices, openNow)
            queryIndex++
        }
    }

    private fun queryYelp(typeOfFood: String, location: String, radius: Int, prices: String, openNow: Boolean){
        val yelpService = YelpService.create()

        yelpService.searchRestaurants("Bearer $YELP_API_KEY", typeOfFood, location, radius, MAX_RESPONSES, prices, openNow)
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
                            && !restaurant.location.address.isNullOrEmpty()
                            && restaurant.image_url.isNotEmpty()){

                            restaurantAllDisplay.add(restaurant)
                            restaurantDisplay.add(restaurant)
                        }
                    }

                    for (rest in restaurantDisplay){
                        Log.i("MAPS", "${rest.name}, ${rest.location.address}")
                    }

                    queryAsNeeded(typeOfFood, radius, prices, openNow)

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
        if (restaurant.id in savedVM.getSavedList()){
            return true
        }
        return false
    }

    private fun isSkipped(restaurant: YelpRestaurant): Boolean{
        if (restaurant.id in savedVM.getSkippedList()){
            return true
        }
        return false
    }

    fun storeRestaurant(restaurant: YelpRestaurant,
                        isSaved: Boolean,
                        typeOfFood: String? = "",
                        destination: String? = "",
                        radius: String? = "",
                        prices: String = "1,2,3,4",
                        openNow: Boolean = false) {

        val currentUser = ParseUser.getCurrentUser()
        val saved = SavedRestaurants()
        saved.setIsSaved(isSaved)
        saved.setUser(currentUser)
        saved.setRestaurantID(restaurant.id)
        saved.setRestaurantName(restaurant.name)
        saved.setRestaurantPrice(restaurant.price)
        saved.setRestaurantPhone(restaurant.phone)
        saved.setRestaurantRating(restaurant.rating)
        saved.setRestaurantImage(restaurant.image_url)
        saved.setRestaurantReviewCount(restaurant.review_count)
        saved.setRestaurantAddress(restaurant.location.address)
        saved.setRestaurantLatitude(restaurant.coordinates.latitude.toDouble())
        saved.setRestaurantLongitude(restaurant.coordinates.longitude.toDouble())
        val categories = categoriesToString(restaurant)
        saved.setRestaurantCategories(categories)
        saved.setYelpUrl(restaurant.url)

        saved.saveInBackground { e ->
            if (e != null) {
                Log.e(TAG, "Error while saving", e)
            }
            Log.i(TAG, "Restaurant save was successful")
        }

        nextRestaurant(restaurant, typeOfFood, destination, radius, prices, openNow)
    }

    private fun nextRestaurant(restaurant: YelpRestaurant, typeOfFood: String?, destination: String?, radius: String?, prices: String, openNow: Boolean) {
        // null check for if you start selecting restaurants without setting filters
        if (typeOfFood == null || destination == null){
            restaurantDisplay.remove(restaurant)
            setStateForNext()
            return
        }

        else if (radius.isNullOrEmpty()){
            nextRestaurant(restaurant, typeOfFood, destination, defaultPathRadius, prices, openNow)
            return
        }

        restaurantDisplay.remove(restaurant)
        setStateForNext()

        if (destination.isNotEmpty()){
            queryAsNeeded(typeOfFood, radius.toInt(), prices, openNow)
        }
    }

    private fun setStateForNext(){
        if (restaurantDisplay.isNotEmpty()){
            _stateFlow.tryEmit(RestaurantState.Success(restaurantDisplay.component1()))
        }
        else{
            _stateFlow.value = RestaurantState.Idle
        }
    }

    fun getRestaurantsToDisplay(): MutableList<YelpRestaurant> {
        return restaurantDisplay
    }

    fun getCurrentRestaurant(): YelpRestaurant {
        return restaurantDisplay.component1()
    }

    fun getPath(): MutableList<LatLng> {
        return polylineCoordinates
    }

    sealed class RestaurantState {
        object Loading : RestaurantState()
        object Idle : RestaurantState()
        data class Success(var restaurant : YelpRestaurant) : RestaurantState()
    }

}

