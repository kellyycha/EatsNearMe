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
import kotlinx.android.synthetic.main.fragment_restaurants.*
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        buttons()
        collectLatestLifecycleFlow(viewModel.stateFlow) {
            when(it){
                is RestaurantsViewModel.RestaurantState.Loading -> {
                    Log.i(TAG, "Loading")
                    spinner.visibility = View.VISIBLE
                }
                is RestaurantsViewModel.RestaurantState.Success -> {
                    Log.i(TAG, "Finished Loading, show restaurant")
                    spinner.visibility = View.GONE
                    show(it.restaurant)
                }
            }
        }
    }

    private fun buttons() {

        btnSearch.setOnClickListener {
            Log.i(TAG, "Clicked Search")
            viewModel.resetStateFlow(etSearchFood.text.toString(), etLocation.text.toString())
        }

        btnLocation.setOnClickListener {
            Log.i(TAG, "Clicked Set Location")
            // TODO: if empty, use current location
            viewModel.resetStateFlow(etSearchFood.text.toString(), etLocation.text.toString())
        }

//        btnRadius.setOnClickListener {
//            Log.i(TAG, "Clicked Set Radius")
//            // TODO: Set Radius
//        }

        btnPrev.setOnClickListener {
            Log.i(TAG, "Clicked Previous")
            viewModel.prevRestaurant()
        }

        btnYes.setOnClickListener(View.OnClickListener {
            Log.i(TAG, "onClick yes button")
            Log.i(TAG, "current restaurant: $restaurant")
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
