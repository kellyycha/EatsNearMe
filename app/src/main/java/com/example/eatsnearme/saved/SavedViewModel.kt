package com.example.eatsnearme.saved

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.eatsnearme.KEY_IS_SAVED
import com.example.eatsnearme.KEY_USER
import com.example.eatsnearme.SavedRestaurants
import com.example.eatsnearme.restaurants.RestaurantsViewModel
import com.example.eatsnearme.yelp.YelpRestaurant
import com.parse.FindCallback
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SavedViewModel : ViewModel() {

    companion object{
        private const val QUERY_LIMIT = 20
        private const val TAG = "SavedViewModel"
    }

    private var savedList = ArrayList<String>()
    private var skippedList = ArrayList<String>()

    private val _stateFlow = MutableStateFlow<SavedState>(SavedState.Loading)
    val stateFlow: StateFlow<SavedState> = _stateFlow

    fun querySaved(allSaved: ArrayList<SavedRestaurants>, adapter: SavedAdapter) {
        Log.i(TAG,"loading saved and skipped")

        _stateFlow.value = SavedState.Loading
        savedList.clear()

        val query: ParseQuery<SavedRestaurants> = ParseQuery.getQuery(SavedRestaurants::class.java)
        query.include(KEY_USER)
            .whereEqualTo(KEY_USER, ParseUser.getCurrentUser())
            .whereEqualTo(KEY_IS_SAVED, true)
            .setLimit(QUERY_LIMIT)
            .addDescendingOrder("createdAt")
            .findInBackground(FindCallback<SavedRestaurants> { restaurants, e ->
                if (e != null) {
                    Log.e(SavedFragment.TAG, "Issue with getting saved restaurants", e)
                    return@FindCallback;
                }
                for (restaurant in restaurants) {
                    Log.i(SavedFragment.TAG, "Saved Restaurant: ${restaurant.getRestaurantName()}")
                    savedList.add(restaurant.getRestaurantName().toString())

                }
                trackSkipped()
                allSaved.addAll(restaurants)
                adapter.notifyDataSetChanged()
                _stateFlow.value = SavedState.Loaded
            })
    }

    private fun trackSkipped() {
        skippedList.clear()
        val query: ParseQuery<SavedRestaurants> = ParseQuery.getQuery(SavedRestaurants::class.java)
        query.include(KEY_USER)
            .whereEqualTo(KEY_USER, ParseUser.getCurrentUser())
            .whereEqualTo(KEY_IS_SAVED, false)
            .findInBackground(FindCallback { restaurants, e ->
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

    sealed class SavedState {
        object Loading : SavedState()
        object Loaded : SavedState()
    }
}