package com.example.eatsnearme.restaurants

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.eatsnearme.R
import com.parse.ParseUser
import kotlinx.android.synthetic.main.fragment_restaurants.*

private const val TAG = "RestaurantsFragment"

open class RestaurantsFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurants, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        buttons()
    }
    private fun buttons() {
        btnYes.setOnClickListener(View.OnClickListener {
            Log.i(TAG, "onClick yes button")
            Log.i(TAG, "restaurants list: $restaurants")
            val currentUser = ParseUser.getCurrentUser()
            GetRestaurants().saveRestaurant(restaurant!!.name, currentUser) // save just restaurant or save pic, name, etc. info separately?
            show()
            GetRestaurants().next()
        })

        btnNo.setOnClickListener(View.OnClickListener {
            Log.i(TAG, "onClick no button")
            show()
            GetRestaurants().next()
        })
    }
    private fun show(){
        if (restaurants.size <= 0){
            Log.i(TAG, "No restaurants in the list")
        }
        else {
            restaurant = restaurants.component1()
            tvName.text = restaurant!!.name
            context?.let {
                Glide.with(it)
                    .load(restaurant!!.image_url)
                    .into(ivYelpPic)
            }
        }
    }
}