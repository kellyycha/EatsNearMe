package com.example.eatsnearme.restaurants

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.example.eatsnearme.R
import com.example.eatsnearme.yelp.YelpRestaurant
import com.parse.ParseUser
import kotlinx.android.synthetic.main.fragment_restaurants.*
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import okhttp3.internal.wait
import kotlin.concurrent.withLock

private const val TAG = "RestaurantsFragment"

open class RestaurantsFragment : Fragment() {
    private lateinit var restaurant: YelpRestaurant
    private val viewModel: RestaurantsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_restaurants, container, false)
    }


    @OptIn(InternalCoroutinesApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        buttons()
        collectLatestLifecycleFlow(viewModel.stateFlow) {
            when(it){
                is RestaurantsViewModel.RestaurantState.Loading -> {
                    Log.i(TAG, "Loading")
                    // TODO: show a spinning wheel
                }
                is RestaurantsViewModel.RestaurantState.Success -> {
                    Log.i(TAG, "Finished Loading, show restaurant")
                    show()
                }
            }
        }
    }

    private fun buttons() {
        btnSearch.setOnClickListener {
            Log.i(TAG, "Clicked Search")
            viewModel.resetStateFlow(etSearchFood.text.toString())
        }

        btnSetLocation.setOnClickListener {
            Log.i(TAG, "Clicked Set Location")
            // Set location on google maps
        }
        btnGo.setOnClickListener {
            Log.i(TAG, "Clicked Go")
            // Set Radius
        }

        // add a back button

        btnYes.setOnClickListener(View.OnClickListener {
            Log.i(TAG, "onClick yes button")
            Log.i(TAG, "current restaurant: $restaurant")
            Log.i(TAG, "list of restaurants: ${viewModel.getRestaurantList()}")
            val currentUser = ParseUser.getCurrentUser()
            viewModel.saveRestaurant(restaurant.name, currentUser) // save just restaurant or save pic, name, etc. info separately?
            viewModel.nextRestaurant()
        })

        btnNo.setOnClickListener(View.OnClickListener {
            Log.i(TAG, "onClick no button")
            viewModel.nextRestaurant()
        })
    }
    private fun show() {
        val restaurants = viewModel.getRestaurantList()
        val index = viewModel.getRestaurantIndex()
        Log.i(TAG, "Index: $index List: $restaurants")
        if (index < restaurants.size) {
            restaurant = viewModel.getRestaurantList()[index]
            tvName.text = restaurant.name
            context?.let {
                Glide.with(it)
                    .load(restaurant.image_url)
                    .into(ivYelpPic)
            }
        }
        else{
            Log.i(TAG, "Can't show")
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
