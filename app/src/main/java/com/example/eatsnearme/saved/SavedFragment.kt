package com.example.eatsnearme.saved

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eatsnearme.KEY_IS_SAVED
import com.example.eatsnearme.KEY_USER
import com.example.eatsnearme.R
import com.example.eatsnearme.SavedRestaurants
import com.example.eatsnearme.restaurants.RestaurantsViewModel
import com.example.eatsnearme.yelp.YelpRestaurant
import com.parse.FindCallback
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.android.synthetic.main.fragment_saved.*

class SavedFragment : Fragment() {

    companion object{
        const val TAG = "SavedFragment"
        private const val QUERY_LIMIT = 20
        val savedList = ArrayList<String>()
        val skippedList = ArrayList<String>()
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
        val adapter = SavedAdapter(requireContext(), allSaved)
        rvSaved.adapter = adapter
        rvSaved.layoutManager = LinearLayoutManager(requireContext())
        querySaved(allSaved, adapter)
    }

    private fun querySaved(allSaved: ArrayList<SavedRestaurants>, adapter: SavedAdapter) {
        savedList.clear()
        val query: ParseQuery<SavedRestaurants> = ParseQuery.getQuery(SavedRestaurants::class.java)
        query.include(KEY_USER)
            .whereEqualTo(KEY_USER, ParseUser.getCurrentUser())
            .whereEqualTo(KEY_IS_SAVED, true)
            .setLimit(QUERY_LIMIT)
            .addDescendingOrder("createdAt")
            .findInBackground(FindCallback<SavedRestaurants> { restaurants, e ->
                if (e != null) {
                    Log.e(TAG, "Issue with getting saved restaurants", e)
                    return@FindCallback;
                }
                for (restaurant in restaurants) {
                    Log.i(TAG, "Saved Restaurant: ${restaurant.getRestaurantName()}")
                    savedList.add(restaurant.getRestaurantName().toString())

                }
                trackSkipped()
                allSaved.addAll(restaurants)
                adapter.notifyDataSetChanged()
            })
    }

    private fun trackSkipped() {
        skippedList.clear()
        val query: ParseQuery<SavedRestaurants> = ParseQuery.getQuery(SavedRestaurants::class.java)
        query.include(KEY_USER)
            .whereEqualTo(KEY_USER, ParseUser.getCurrentUser())
            .whereEqualTo(KEY_IS_SAVED, false)
            .findInBackground(FindCallback<SavedRestaurants> { restaurants, e ->
                if (e != null) {
                    Log.e(TAG, "Issue with getting skipped restaurants", e)
                    return@FindCallback;
                }
                for (restaurant in restaurants) {
                    Log.i(TAG, "Skipped Restaurant: ${restaurant.getRestaurantName()}")
                    skippedList.add(restaurant.getRestaurantName().toString())

                }
            })
    }

    fun getSavedList(): ArrayList<String> {
        return savedList
    }

    fun getSkippedList(): ArrayList<String> {
        return skippedList
    }


}
