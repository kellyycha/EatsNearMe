package com.example.eatsnearme.restaurants

//import kotlinx.android.synthetic.main.fragment_restaurants.*
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.eatsnearme.R
import com.example.eatsnearme.yelp.YelpRestaurant
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.lorentzos.flingswipe.SwipeFlingAdapterView
import com.lorentzos.flingswipe.SwipeFlingAdapterView.onFlingListener
import kotlinx.android.synthetic.main.bottom_sheet_filter.*
import kotlinx.android.synthetic.main.fragment_cardswipe.*
import kotlinx.android.synthetic.main.fragment_saved.*
import kotlinx.android.synthetic.main.item_card.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.internal.notify


class RestaurantsFragment : Fragment() {
    private lateinit var restaurant: YelpRestaurant

    private val viewModel: RestaurantsViewModel by activityViewModels()

    private var typeOfFood : String? = null
    private var location : String? = null
    private var destination : String? = null
    private var radius : String? = null

    companion object {
        const val TAG = "RestaurantsFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_cardswipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requestPermissionsIfNeed(requireContext())

        btnFilter.setOnClickListener{
            showBottomSheet()
        }

        collectLatestLifecycleFlow(viewModel.stateFlow) {
            when(it){
                is RestaurantsViewModel.RestaurantState.Loading -> {
                    Log.i(TAG, "Loading restaurants")
                    spinner.visibility = View.VISIBLE
                }
                is RestaurantsViewModel.RestaurantState.Idle -> {
                    Log.i(TAG, "idle state")
                    Toast.makeText(requireContext(), "No more restaurants to show", Toast.LENGTH_SHORT).show()
                    spinner.visibility = View.GONE
                }
                is RestaurantsViewModel.RestaurantState.Success -> {
                    restaurant = it.restaurant
                    Log.i(TAG, "Finished Loading, show restaurant")
                    spinner.visibility = View.GONE
                    swipeCard(view)
                }
            }
        }
    }

    private fun swipeCard(view: View) {
        val swipeFlingAdapterView = view.findViewById<SwipeFlingAdapterView>(R.id.swipeFlingAdapterView)

        val arrayAdapter = CardAdapter(requireContext(), R.layout.item_card, viewModel.getRestaurantsToDisplay())

        Log.i(TAG, "Restaurant Names: ${viewModel.getRestaurantNames()}")

        swipeFlingAdapterView.adapter = arrayAdapter

        swipeFlingAdapterView.setFlingListener(object : onFlingListener {
            override fun removeFirstObjectInAdapter() {
                viewModel.getRestaurantNames().removeAt(0)
                arrayAdapter.notifyDataSetChanged()
            }

            override fun onLeftCardExit(dataObject: Any) {
                Log.i(TAG, "swipe no")
                viewModel.storeRestaurant(restaurant, false,
                    typeOfFood = typeOfFood,
                    destination = destination,
                    radius = radius)
            }

            override fun onRightCardExit(dataObject: Any) {
                Log.i(TAG, "swipe yes")
                viewModel.storeRestaurant(restaurant,true,
                    typeOfFood = typeOfFood,
                    destination = destination,
                    radius = radius)
            }

            override fun onAdapterAboutToEmpty(itemsInAdapter: Int) {

            }

            override fun onScroll(p0: Float) {

            }
        })
    }

    private fun showBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_filter, null)

        val btnSearch = bottomSheetView.findViewById<FloatingActionButton>(R.id.btnSearch)
        val btnExitFilter = bottomSheetView.findViewById<ImageButton>(R.id.btnExitFilter)
        val etSearchFood = bottomSheetView.findViewById<EditText>(R.id.etSearchFood)
        val etLocation = bottomSheetView.findViewById<EditText>(R.id.etLocation)
        val etDestination = bottomSheetView.findViewById<EditText>(R.id.etDestination)
        val etRadius = bottomSheetView.findViewById<EditText>(R.id.etRadius)

        val dialog = BottomSheetDialog(requireContext())

        dialog.setCancelable(false)
        dialog.setContentView(bottomSheetView)
        dialog.show()

        btnSearch.setOnClickListener {
            Log.i(TAG, "Clicked Search")

            typeOfFood = etSearchFood.text.toString()
            location = etLocation.text.toString()
            destination = etDestination.text.toString()
            radius = etRadius.text.toString()

            viewModel.fetchRestaurants(
                typeOfFood = typeOfFood!!,
                location = location!!,
                destination = destination!!,
                radius = radius!!)
            dialog.dismiss()
        }

        btnExitFilter.setOnClickListener {
            dialog.dismiss()
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
                viewModel.fetchRestaurants()
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
