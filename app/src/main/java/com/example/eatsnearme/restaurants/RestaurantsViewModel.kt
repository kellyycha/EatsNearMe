package com.example.eatsnearme.restaurants

import android.app.Application
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.eatsnearme.SavedRestaurants
import com.example.eatsnearme.saved.SavedFragment
import com.example.eatsnearme.yelp.*
import com.parse.ParseUser
import com.parse.SaveCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RestaurantsViewModel(application: Application) : AndroidViewModel(application) {

    val restaurantResults = mutableListOf<YelpRestaurant>()
    val restaurantDisplay = mutableListOf<YelpRestaurant>()

    private val _stateFlow = MutableStateFlow<RestaurantState>(RestaurantState.Loading)
    val stateFlow:StateFlow<RestaurantState> = _stateFlow

    private var index = 0
    private var currLocation = ""

    companion object {
        const val TAG = "RestaurantsViewModel"
        const val defaultRadius = "1000"
    }

    fun nextRestaurant() {
        if (restaurantDisplay.size > index+1){
            index++
            _stateFlow.tryEmit(RestaurantState.Success(restaurantDisplay[index]))
        }
        else{
            Toast.makeText(getApplication(), "No more restaurants to show", Toast.LENGTH_SHORT).show()
        }

    }

    fun prevRestaurant() {
        if (index > 0){
            index--
            _stateFlow.tryEmit(RestaurantState.Success(restaurantDisplay[index]))
        }
        else{
            Toast.makeText(getApplication(), "Cannot show previous", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchRestaurantsForCurrentLocation(typeOfFood: String, radius: Int) {
        LocationService().getLastLocation(getApplication(), object : LocationService.MyLocationListener{
            override fun onLocationChanged(location: Location) {
                Log.d(TAG,"Coordinates: ${location.latitude}, ${location.longitude}")
                currLocation = "${location.latitude}, ${location.longitude}"
                queryYelp(typeOfFood, currLocation, radius)
            }
        })

    }

    init {
        Log.i(TAG, "init")
        // TODO: load saved restaurants before fetching.
        //  right now it only filters after clicking saved tab and going back and then clicking search again
        fetchRestaurants("","", defaultRadius)
    }

    fun fetchRestaurants(typeOfFood: String, location: String, radius: String) {
        Log.i(TAG, "type of food: $typeOfFood")
        Log.i(TAG, "Location: $location")
        Log.i(TAG, "radius: $radius")

        index = 0
        restaurantResults.clear()
        restaurantDisplay.clear()
        _stateFlow.value = RestaurantState.Loading

        if (radius.isEmpty()){
            fetchRestaurants(typeOfFood, location, defaultRadius)
            return
        }

        if(location.isEmpty() && LocationService().hasPermissions(getApplication())){
            Log.i(TAG, "no location")
            fetchRestaurantsForCurrentLocation(typeOfFood, radius.toInt())
        }
        else if (location.isNotEmpty()){
            queryYelp(typeOfFood, location, radius.toInt())
        }
        else{
            _stateFlow.value = RestaurantState.Idle
        }

    }

    private fun queryYelp(typeOfFood: String, location: String, radius: Int){
        Log.i(TAG, "query yelp")
        val yelpService = YelpService.create()
        yelpService.searchRestaurants("Bearer $API_KEY", typeOfFood, location, radius)
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
                        if (!isSkipped(restaurant) && !isSaved(restaurant)){
                            restaurantDisplay.add(restaurant)
                        }
                    }

                    Log.i(TAG, "all restaurants: $restaurantResults")
                    Log.i(TAG, "display restaurants: $restaurantDisplay")
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
        saved.setRestaurantPrice(restaurant.price)
        saved.setRestaurantRating(restaurant.rating)
        saved.setRestaurantImage(restaurant.image_url)
        saved.setRestaurantDistance(restaurant.distance_meters)
        saved.setRestaurantReviewCount(restaurant.review_count)

        saved.saveInBackground(SaveCallback { e ->
            if (e != null) {
                Log.e(TAG, "Error while saving", e)
                Toast.makeText(getApplication(), "Error while saving", Toast.LENGTH_SHORT).show()
            }
            Log.i(TAG, "Restaurant save was successful")
        })

        nextRestaurant()
    }


    sealed class RestaurantState {
        object Loading : RestaurantState()
        object Idle : RestaurantState()
        class Success(var restaurant : YelpRestaurant) : RestaurantState()
    }

}