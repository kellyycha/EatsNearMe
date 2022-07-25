package com.example.eatsnearme.saved

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eatsnearme.KEY_IS_SAVED
import com.example.eatsnearme.KEY_USER
import com.example.eatsnearme.R
import com.example.eatsnearme.SavedRestaurants
import com.example.eatsnearme.restaurants.LocationService
import com.example.eatsnearme.restaurants.RestaurantsFragment
import com.example.eatsnearme.restaurants.RestaurantsViewModel
import com.example.eatsnearme.restaurants.collectLatestLifecycleFlow
import com.example.eatsnearme.yelp.YelpRestaurant
import com.parse.FindCallback
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.android.synthetic.main.fragment_cardswipe.*
import kotlinx.android.synthetic.main.fragment_saved.*

class SavedFragment : Fragment() {

    private val viewModel: SavedViewModel by viewModels()

    companion object{
        const val TAG = "SavedFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_saved, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val allSaved = ArrayList<SavedRestaurants>()
        val adapter = SavedAdapter(requireActivity(), allSaved)

        rvSaved.adapter = adapter
        rvSaved.layoutManager = LinearLayoutManager(requireContext())

        viewModel.querySaved(allSaved, adapter)

        collectLatestLifecycleFlow(viewModel.stateFlow) { it ->
            when(it){
                is SavedViewModel.SavedState.Loading -> {
                    Log.i(RestaurantsFragment.TAG, "Loading restaurants")
                    savedSpinner.visibility = View.VISIBLE
                }
                is SavedViewModel.SavedState.Loaded -> {
                    Log.i(RestaurantsFragment.TAG, "Loaded all")
                    savedSpinner.visibility = View.GONE
                }
            }
        }
    }

}
