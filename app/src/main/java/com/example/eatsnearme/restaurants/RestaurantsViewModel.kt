package com.example.eatsnearme.restaurants

import android.util.Log
import android.widget.Toast
import com.example.eatsnearme.SavedRestaurants
import com.example.eatsnearme.yelp.API_KEY
import com.example.eatsnearme.yelp.YelpRestaurant
import com.example.eatsnearme.yelp.YelpService
import com.parse.ParseUser
import com.parse.SaveCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.lifecycle.ViewModel
import com.example.eatsnearme.yelp.YelpSearchResult
import kotlinx.coroutines.flow.*

private const val TAG = "GetRestaurants"

class RestaurantsViewModel : ViewModel() {

    val restaurants = mutableListOf<YelpRestaurant>()

    private val _stateFlow = MutableStateFlow(0)
    val stateFlow = _stateFlow.asStateFlow()

    fun nextRestaurant() {
        if (_stateFlow.value < restaurants.size) {
            _stateFlow.value += 1
        }
        else {
            Log.i(TAG, "no more restaurants in the list")
        }
    }

    fun resetStateFlow(typeOfFood: String) {
        fetchRestaurants(typeOfFood)
        _stateFlow.value = 0
    }

    init {
        // TODO: When I change tabs and go back, init is called again and restaurants automatically clears. how do I prevent this?
        Log.i(TAG, "init")
        if (restaurants.size == 0){
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
                    restaurants.clear()
                    restaurants.addAll(body.restaurants)
                    Log.i(TAG, "restaurants: $restaurants")
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

