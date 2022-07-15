package com.example.eatsnearme.restaurants

import android.app.Application
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.eatsnearme.SavedRestaurants
import com.example.eatsnearme.yelp.*
import com.parse.ParseUser
import com.parse.SaveCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RestaurantsViewModel(application: Application) : AndroidViewModel(application) {

    val restaurants = mutableListOf<YelpRestaurant>()

    private val _stateFlow = MutableStateFlow<RestaurantState>(RestaurantState.Loading)
    val stateFlow:StateFlow<RestaurantState> = _stateFlow

    private var index = 0
    private var currLocation = ""

    companion object {
        const val TAG = "RestaurantsViewModel"
        const val defaultRadius = "1000"
    }

    fun nextRestaurant() {
        if (restaurants.size > index+1){
            index++
            _stateFlow.tryEmit(RestaurantState.Success(restaurants[index]))
        }
        else{
            Toast.makeText(getApplication(), "No more restaurants to show", Toast.LENGTH_SHORT).show()
        }

    }

    fun prevRestaurant() {
        if (index > 0){
            index--
            _stateFlow.tryEmit(RestaurantState.Success(restaurants[index]))
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
        fetchRestaurants("","", defaultRadius)
    }

    // TODO: if radius empty, use 0.5 miles (800 meters)
    fun fetchRestaurants(typeOfFood: String, location: String, radius: String) {
        Log.i(TAG, "type of food: $typeOfFood")
        Log.i(TAG, "Location: $location")
        Log.i(TAG, "radius: $radius")

        index = 0
        restaurants.clear()
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

                    //TODO: check if restaurant already saved

                    Log.i(TAG, "Adding restaurants...")
                    restaurants.addAll(body.restaurants)
                    Log.i(TAG, "loaded restaurants: $restaurants")
                    if (restaurants.isEmpty()){
                        _stateFlow.value = RestaurantState.Idle
                        Toast.makeText(getApplication(), "No restaurants within radius", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        _stateFlow.value = RestaurantState.Success(restaurants[index])
                    }


                }

                override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                    Log.i(TAG, "onFailure $t")
                }

            })
    }

    fun saveRestaurant(restaurantName: String) {
        val currentUser = ParseUser.getCurrentUser()
        val saved = SavedRestaurants()
        saved.setUser(currentUser)
        saved.setRestaurantName(restaurantName)

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