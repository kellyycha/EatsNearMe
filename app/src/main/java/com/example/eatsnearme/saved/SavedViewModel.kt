package com.example.eatsnearme.saved

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.eatsnearme.KEY_IS_SAVED
import com.example.eatsnearme.KEY_USER
import com.example.eatsnearme.SavedRestaurants
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

    private var savedIdList = ArrayList<String>()
    private var skippedIdList = ArrayList<String>()
    private val allSaved = mutableListOf<SavedRestaurants>()

    private val _stateFlow = MutableStateFlow<SavedState>(SavedState.Loading)
    val stateFlow: StateFlow<SavedState> = _stateFlow

    fun querySaved() {
        Log.i(TAG,"loading saved and skipped")

        _stateFlow.value = SavedState.Loading
        savedIdList.clear()
        allSaved.clear()

        val query: ParseQuery<SavedRestaurants> = ParseQuery.getQuery(SavedRestaurants::class.java)
        query.include(KEY_USER)
            .whereEqualTo(KEY_USER, ParseUser.getCurrentUser())
            .whereEqualTo(KEY_IS_SAVED, true)
            .setLimit(QUERY_LIMIT)
            .addDescendingOrder("createdAt")
            .findInBackground(FindCallback { restaurants, e ->
                if (e != null) {
                    Log.e(SavedFragment.TAG, "Issue with getting saved restaurants", e)
                    return@FindCallback;
                }
                for (restaurant in restaurants) {
                    Log.i(SavedFragment.TAG, "Saved Restaurant: ${restaurant.getRestaurantName()}")
                    savedIdList.add(restaurant.getRestaurantID().toString())

                }
                trackSkipped()
                allSaved.addAll(restaurants)
                _stateFlow.value = SavedState.Loaded(allSaved)
            })
    }

    private fun trackSkipped() {
        skippedIdList.clear()
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
                    skippedIdList.add(restaurant.getRestaurantID().toString())

                }
            })
    }

    fun getSavedList(): ArrayList<String> {
        return savedIdList
    }

    fun getSkippedList(): ArrayList<String> {
        return skippedIdList
    }

    sealed class SavedState {
        object Loading : SavedState()
        data class Loaded(var allSaved : MutableList<SavedRestaurants>): SavedState()
    }
}