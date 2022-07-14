package com.example.eatsnearme.restaurants

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.eatsnearme.MainActivity
import com.example.eatsnearme.R
import com.example.eatsnearme.yelp.YelpRestaurant
import kotlinx.android.synthetic.main.fragment_restaurants.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class RestaurantsFragment : Fragment() {
    private lateinit var restaurant: YelpRestaurant
    private lateinit var currLocation: String

    private val viewModel: RestaurantsViewModel by viewModels()

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
        requestPermissionsIfNeed(requireContext())

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

            //viewModel.getLastCoordinates(requireContext())

//            //I want to get a new current location when I click.
//            if (etLocation.text.isEmpty()){
//                viewModel.fetchRestaurants(etSearchFood.text.toString(), currLocation)
//            }
//            else{
            viewModel.fetchRestaurants(etSearchFood.text.toString(), etLocation.text.toString())
//            }
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

    // this is called every time, so when there is permission, it gets the coordinates so resets. have to fix
    private fun requestPermissionsIfNeed(context: Context) {
        Log.i(TAG, "requesting")
        if (hasPermissions(context)) {
            Log.i(TAG,"has permissions")
            viewModel.getLastCoordinates(context)
            return
        }
        else {
            Log.i(TAG,"need to request permissions")
            requestPermission(context)
        }
    }

    private fun hasPermissions(context: Context): Boolean {
        Log.i(TAG, "Checking Permissions")
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission(context: Context) {
        Log.i(TAG, "Requesting Permissions")
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LocationService.PERMISSION_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(TAG, "on Activity Result")
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(TAG, "Request Permission Result")
        if(requestCode == LocationService.PERMISSION_REQUEST_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.i(TAG, "Permission Granted")
                requestPermission(requireContext())
//                viewModel.getLastCoordinates(requireContext())
            }
            else {
                Log.i(TAG, "Permission Denied")
                requestPermission(requireContext())
            }
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
