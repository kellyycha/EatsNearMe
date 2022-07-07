package com.example.eatsnearme.restaurants

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.eatsnearme.SavedRestaurants
import com.example.eatsnearme.yelp.API_KEY
import com.example.eatsnearme.yelp.YelpRestaurant
import com.example.eatsnearme.yelp.YelpSearchResult
import com.example.eatsnearme.yelp.YelpService
import com.parse.ParseUser
import com.parse.SaveCallback
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import okhttp3.internal.notify
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

private const val TAG = "GetRestaurants"

class RestaurantsViewModel : ViewModel() {
    var loaded = false
    val restaurants = mutableListOf<YelpRestaurant>()

    private val _stateFlow = MutableStateFlow(0)
    val stateFlow= _stateFlow

    fun nextRestaurant() {
        if (_stateFlow.value < restaurants.size) {
            _stateFlow.value += 1
        }
        else {
            Log.i(TAG, "no more restaurants in the list")
        }
    }

    fun resetStateFlow(typeOfFood: String) {
        _stateFlow.value = 0
        restaurants.clear()
        loaded = false
        fetchRestaurants(typeOfFood)
    }

    init {
        Log.i(TAG, "init")
        if (restaurants.isEmpty()){
            fetchRestaurants("")
        }
    }

    private fun fetchRestaurants(typeOfFood: String) {
        Log.i(TAG, "type of food: $typeOfFood")
        val yelpService = YelpService.create()
        yelpService.searchRestaurants("Bearer $API_KEY", typeOfFood, "San Francisco")
            .enqueue(object : Callback<YelpSearchResult> {
                override fun onResponse(call: Call<YelpSearchResult>, response: Response<YelpSearchResult>) {
                    Log.i(TAG, "onResponse $response")
                    val body = response.body()
                    if (body == null) {
                        Log.w(TAG, "did not receive valid response body from Yelp API")
                        return
                    }

//                    //TODO: check if restaurant already saved
//                    for (restaurant in body.restaurants){
//                        Log.i(TAG, "looping through restaurants: $restaurant")
//                        Log.i(TAG, "saved: ${SavedFragment().getSavedNamesList()}")
//                        for (saved in SavedFragment().getSavedNamesList()){
//                            Log.i(TAG, "looping through saved: $saved")
//                            if (restaurant.name == saved){
//                                Log.i(TAG, "restaurant already saved")
//                            }
//                            else{
//                                restaurants.add(restaurant)
//                            }
//                        }
//                    }
                    Log.i(TAG, "Adding restaurants...")
                    restaurants.addAll(body.restaurants)
                    loaded = true
                    Log.i(TAG, "loaded restaurants: $restaurants")

                }

                override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                    Log.i(TAG, "onFailure $t")
                }

            })
    }

    fun saveRestaurant(restaurantName: String, currentUser: ParseUser?) {
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
    }

    fun getRestaurantList() : MutableList<YelpRestaurant> {
        return restaurants
    }
}
