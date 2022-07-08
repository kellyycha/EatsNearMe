package com.example.eatsnearme.restaurants

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.eatsnearme.MainActivity
import com.example.eatsnearme.SavedRestaurants
import com.example.eatsnearme.yelp.*
import com.google.android.material.internal.ContextUtils.getActivity
import com.parse.ParseUser
import com.parse.SaveCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "GetRestaurants"

class RestaurantsViewModel : ViewModel() {
    val restaurants = mutableListOf<YelpRestaurant>()

    private val _stateFlow = MutableStateFlow<RestaurantState>(RestaurantState.Loading)
    val stateFlow:StateFlow<RestaurantState> = _stateFlow

    var index = 0

    fun nextRestaurant() {
        index++
        Log.i(TAG,"index: $index")
        _stateFlow.tryEmit(RestaurantState.Success(restaurants[index]))
    }

    fun prevRestaurant() {
        if (index > 0){
            index--
            Log.i(TAG,"index: $index")
            _stateFlow.tryEmit(RestaurantState.Success(restaurants[index]))
        }
    }

    fun searchRestaurants(typeOfFood: String, location: String) {
        index = 0
        restaurants.clear()
        _stateFlow.value = RestaurantState.Loading
        fetchRestaurants(typeOfFood, location)
    }

    init {
        LocationService().getCurrentLocation()
        fetchRestaurants("", LocationService().getCoordinates())
    }

    // TODO: add radius parameter
    private fun fetchRestaurants(typeOfFood: String, location: String) {
        Log.i(TAG, "type of food: $typeOfFood")
        Log.i(TAG, "Location: $location")

        val yelpService = YelpService.create()
        yelpService.searchRestaurants("Bearer $API_KEY", typeOfFood, location)
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

                    _stateFlow.value = RestaurantState.Success(restaurants[index])

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
                Toast.makeText(
                    RestaurantsFragment().context,
                    "Error while saving",
                    Toast.LENGTH_SHORT
                ).show()
            }
            Log.i(TAG, "Restaurant save was successful")
        })

        nextRestaurant()
    }

    sealed class RestaurantState {
        object Loading : RestaurantState()
        class Success(var restaurant : YelpRestaurant) : RestaurantState()
    }

}