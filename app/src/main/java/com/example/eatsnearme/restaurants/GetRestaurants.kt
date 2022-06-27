package com.example.eatsnearme.restaurants

import android.app.ProgressDialog.show
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.eatsnearme.MainActivity
import com.example.eatsnearme.R
import com.example.eatsnearme.SavedRestaurants
import com.example.eatsnearme.yelp.API_KEY
import com.example.eatsnearme.yelp.YelpRestaurant
import com.example.eatsnearme.yelp.YelpSearchResult
import com.example.eatsnearme.yelp.YelpService
import com.parse.ParseUser
import com.parse.SaveCallback
import kotlinx.android.synthetic.main.fragment_restaurants.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "GetRestaurants"
val restaurants = mutableListOf<YelpRestaurant>()
var restaurant: YelpRestaurant? = null

class GetRestaurants {

    fun fetchRestaurants() {
        val yelpService = YelpService.create()
        yelpService.searchRestaurants("Bearer $API_KEY", "Mexican", "San Francisco")
            .enqueue(object : Callback<YelpSearchResult> {

                override fun onResponse(call: Call<YelpSearchResult>, response: Response<YelpSearchResult>) {
                    Log.i(TAG, "onResponse $response")
                    val body = response.body()
                    if (body == null) {
                        Log.w(TAG, "did not receive valid response body from Yelp API")
                        return
                    }
                    restaurants.addAll(body.restaurants)
                    Log.i(TAG, "restaurants: $restaurants")
                }

                override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                    Log.i(TAG, "onFailure $t")
                }

            })
    }

    fun next() {
        if (restaurants.size > 0) {
            restaurants.removeAt(0)
        }
        else {
            Log.i(TAG, "No more restaurants")
        }
    }

    fun saveRestaurant(restaurantName: String, currentUser: ParseUser?) {
        val saved = SavedRestaurants()
        saved.setUser(currentUser)
        saved.setRestaurantName(restaurantName)

        saved.saveInBackground(SaveCallback { e ->
            if (e != null) {
                Log.e(TAG, "Error while saving", e)
                Toast.makeText(RestaurantsFragment().context, "Error while saving", Toast.LENGTH_SHORT).show()
            }
            Log.i(TAG, "Restaurant save was successful")
        })

    }


}