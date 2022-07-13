package com.example.eatsnearme.restaurants

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.eatsnearme.R
import com.example.eatsnearme.yelp.YelpRestaurant
import kotlinx.android.synthetic.main.fragment_restaurants.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest


class RestaurantsFragment : Fragment() {
    private lateinit var restaurant: YelpRestaurant
    private lateinit var currLocation: String
    //private val viewModel: RestaurantsViewModel by viewModels()

    private val viewModel: RestaurantsViewModel by lazy {
        val activity = requireNotNull(this.activity) {
        }
        ViewModelProviders.of(this, RestaurantsViewModel.Factory(activity.application))
            .get(RestaurantsViewModel::class.java)
    }

    companion object {
        const val TAG = "RestaurantsFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_restaurants, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        collectLatestLifecycleFlow(viewModel.locStateFlow) {
            when(it){
                is RestaurantsViewModel.LocationState.Loading -> {
                    Log.i(TAG, "Loading location")
                    spinner.visibility = View.VISIBLE
                }
                is RestaurantsViewModel.LocationState.Success -> {
                    currLocation = it.coordinates
                    Log.i(TAG, "Finished Loading, got coordinates")
                    spinner.visibility = View.GONE
                    buttons(currLocation)

                }
            }
        }
        collectLatestLifecycleFlow(viewModel.stateFlow) {
            when(it){
                is RestaurantsViewModel.RestaurantState.Loading -> {
                    Log.i(TAG, "Loading restaurants")
                    spinner.visibility = View.VISIBLE
                }
                is RestaurantsViewModel.RestaurantState.Success -> {
                    restaurant = it.restaurant
                    Log.i(TAG, "Finished Loading, show restaurant")
                    spinner.visibility = View.GONE
                    show(restaurant)
                }
            }
        }
    }

    private fun buttons(currLocation: String) {

        btnSearch.setOnClickListener {
            Log.i(TAG, "Clicked Search")
            // TODO: if radius empty, use 0.5 miles
            if (etLocation.text.isEmpty()){
                viewModel.searchRestaurants(etSearchFood.text.toString(), currLocation)
            }
            else{
                viewModel.searchRestaurants(etSearchFood.text.toString(), etLocation.text.toString())
            }
        }

        btnPrev.setOnClickListener {
            Log.i(TAG, "Clicked Previous")
            viewModel.prevRestaurant()
        }

        btnYes.setOnClickListener(View.OnClickListener {
            Log.i(TAG, "onClick yes button")
            viewModel.saveRestaurant(restaurant.name) // save just restaurant or save pic, name, etc. info separately?
        })

        btnNo.setOnClickListener(View.OnClickListener {
            Log.i(TAG, "onClick no button")
            viewModel.nextRestaurant()
        })
    }

    private fun show(restaurant: YelpRestaurant) {
        tvName.text = restaurant.name
        context?.let {
            Glide.with(it)
                .load(restaurant.image_url)
                .into(ivYelpPic)
        }
    }
}

fun <T> Fragment.collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED){
            flow.collectLatest(collect)
        }
    }
}
