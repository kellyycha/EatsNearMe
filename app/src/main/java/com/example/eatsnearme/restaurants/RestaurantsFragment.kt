package com.example.eatsnearme.restaurants

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.eatsnearme.R
import com.example.eatsnearme.saved.SavedFragment
import com.example.eatsnearme.yelp.YelpRestaurant
import kotlinx.android.synthetic.main.fragment_restaurants.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class RestaurantsFragment : Fragment() {
    private lateinit var restaurant: YelpRestaurant

    private val viewModel: RestaurantsViewModel by activityViewModels()

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

        initializeCardButtons()

        btnSearch.setOnClickListener {
            Log.i(TAG, "Clicked Search")
            viewModel.fetchRestaurants(etSearchFood.text.toString(), etLocation.text.toString(), etDestination.text.toString(), etRadius.text.toString())
        }

        collectLatestLifecycleFlow(viewModel.stateFlow) {
            when(it){
                is RestaurantsViewModel.RestaurantState.Loading -> {
                    Log.i(TAG, "Loading restaurants")
                    spinner.visibility = View.VISIBLE
                    hideCardUI()
                }
                is RestaurantsViewModel.RestaurantState.Idle -> {
                    Log.i(TAG, "no location - idle state")
                    spinner.visibility = View.GONE
                    hideCardUI()
                }
                is RestaurantsViewModel.RestaurantState.Success -> {
                    restaurant = it.restaurant
                    Log.i(TAG, "Finished Loading, show restaurant")
                    spinner.visibility = View.GONE
                    showCardUI(restaurant)

                }
            }
        }
    }

    private fun initializeCardButtons() {
//        btnPrev.setOnClickListener {
//            Log.i(TAG, "Clicked Previous")
//            viewModel.prevRestaurant()
//        }

        btnYes.setOnClickListener(View.OnClickListener {
            Log.i(TAG, "onClick yes button")
            viewModel.storeRestaurant(restaurant,true)
        })

        btnNo.setOnClickListener(View.OnClickListener {
            Log.i(TAG, "onClick no button")
            viewModel.storeRestaurant(restaurant, false)
        })
    }

    private fun hideCardUI(){
//        btnPrev.visibility = View.GONE
        btnYes.visibility = View.GONE
        btnNo.visibility = View.GONE
        tvName.visibility = View.GONE
        ivYelpPic.visibility = View.GONE
        ratingBar.visibility = View.GONE
        tvPrice.visibility = View.GONE
    }

    private fun showCardUI(restaurant: YelpRestaurant) {
//        btnPrev.visibility = View.VISIBLE
        btnYes.visibility = View.VISIBLE
        btnNo.visibility = View.VISIBLE
        tvName.visibility = View.VISIBLE
        ivYelpPic.visibility = View.VISIBLE
        ratingBar.visibility = View.VISIBLE
        tvPrice.visibility = View.VISIBLE

        tvName.text = restaurant.name
        tvPrice.text = restaurant.price
        ratingBar.rating = restaurant.rating.toFloat()
        context?.let {
            Glide.with(it)
                .load(restaurant.image_url)
                .into(ivYelpPic)
        }
    }

    private fun requestPermissionsIfNeed(context: Context) {
        Log.i(TAG, "requesting")
        if (LocationService().hasPermissions(context)) {
            Log.i(TAG,"has permissions")
            return
        }
        Log.i(TAG,"need to request permissions")
        requestPermission(context)
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

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.i(TAG, "Request Permission Result")
        if(requestCode == LocationService.PERMISSION_REQUEST_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.i(TAG, "Permission Granted")
                viewModel.fetchRestaurants("","", "", RestaurantsViewModel.defaultRadius)
            }
            else {
                Log.i(TAG, "Permission Denied")
                if (etLocation.text.toString().isEmpty()){
                    Toast.makeText(context, "Enter a location", Toast.LENGTH_SHORT).show()
                }
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
