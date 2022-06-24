package com.example.eatsnearme.restaurants

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
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

private const val TAG = "RestaurantsFragment"

class RestaurantsFragment : Fragment() {
    val restaurants = mutableListOf<YelpRestaurant>()
    var restaurant: YelpRestaurant? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurants, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fetchRestaurants()
        buttons()
    }
    private fun buttons() {
        btnYes.setOnClickListener(View.OnClickListener {
            Log.i(TAG, "onClick yes button")
            restaurants.removeAt(0)
            Log.i(TAG, "restaurants list: $restaurants")
            fetchRestaurants()

            val currentUser = ParseUser.getCurrentUser()
            saveRestaurant(restaurant!!.name, currentUser) // save just restaurant or save pic, name, etc. info separately?
        })

        btnNo.setOnClickListener(View.OnClickListener {
            Log.i(TAG, "onClick no button")
            restaurants.removeAt(0)
            fetchRestaurants()
        })
    }

    private fun saveRestaurant(restaurantName: String, currentUser: ParseUser?) {
        val saved = SavedRestaurants()
        saved.setUser(currentUser)
        saved.setRestaurantName(restaurantName)

        saved.saveInBackground(SaveCallback { e ->
            if (e != null) {
                Log.e(TAG, "Error while saving", e)
                Toast.makeText(context, "Error while saving", Toast.LENGTH_SHORT).show()
            }
            Log.i(TAG, "Restaurant save was successful")
        })

    }

    private fun fetchRestaurants() {
        val yelpService = YelpService.create()
        yelpService.searchRestaurants("Bearer $API_KEY", "Ramen", "San Fransisco")
            .enqueue(object : Callback<YelpSearchResult> {

            override fun onResponse(call: Call<YelpSearchResult>, response: Response<YelpSearchResult>){
                Log.i(TAG, "onResponse $response")
                val body = response.body()
                if (body == null){
                    Log.w(TAG, "did not receive valid response body from Yelp API")
                    return
                }
                restaurants.addAll(body.restaurants)
                Log.i(TAG, "restaurants: $restaurants")
                restaurant = restaurants.component1()
                tvName.text = restaurant!!.name
                context?.let {
                    Glide.with(it)
                        .load(restaurants.component1().image_url)
                        .into(ivYelpPic)
                }
            }

            override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                Log.i(TAG, "onFailure $t")
            }

        })
    }
}