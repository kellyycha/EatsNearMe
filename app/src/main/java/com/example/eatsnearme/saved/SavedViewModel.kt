package com.example.eatsnearme.saved

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.eatsnearme.parse.KEY_USER
import com.example.eatsnearme.parse.SavedRestaurants
import com.parse.FindCallback
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SavedViewModel : ViewModel() {

    companion object{
        private const val TAG = "SavedViewModel"
    }

    private var savedIdList = ArrayList<String>()
    private var skippedIdList = ArrayList<String>()
    private val allSaved = mutableListOf<SavedRestaurants>()
    private val allStored = mutableListOf<SavedRestaurants>()

    private val _stateFlow = MutableStateFlow<SavedState>(SavedState.Loading)
    val stateFlow: StateFlow<SavedState> = _stateFlow

    fun querySaved() {
        _stateFlow.value = SavedState.Loading
        allStored.clear()

        val query: ParseQuery<SavedRestaurants> = ParseQuery.getQuery(SavedRestaurants::class.java)
        query.include(KEY_USER)
            .whereEqualTo(KEY_USER, ParseUser.getCurrentUser())
            .addDescendingOrder("createdAt")
            .findInBackground(FindCallback { restaurants, e ->
                if (e != null) {
                    Log.e(TAG, "Issue with getting saved restaurants", e)
                    return@FindCallback;
                }
                allStored.addAll(restaurants)
                Log.i(TAG,"stored all: ${allStored.size}")
                getSavedList()
            })
    }

    fun removeItemAt(position: Int){
        _stateFlow.value = SavedState.Update
        allSaved[position].deleteInBackground { e ->
            if (e == null) {
                Log.i(TAG, "deleted")
                allSaved.removeAt(position)
                _stateFlow.value = SavedState.Loaded(allSaved)
            }
            else{
                Log.i(TAG, "delete failed")
            }
        }
    }

    fun getSavedList(): ArrayList<String> {
        savedIdList.clear()
        allSaved.clear()
        for (restaurant in allStored){
            if (restaurant.getIsSaved()){
                allSaved.add(restaurant)
                savedIdList.add(restaurant.getRestaurantID().toString())
            }
        }
        Log.i("SAVED","Saved Len: ${allSaved.size}")
        _stateFlow.value = SavedState.Loaded(allSaved)
        return savedIdList
    }

    fun getSkippedList(): ArrayList<String> {
        skippedIdList.clear()
        for (restaurant in allStored){
            if (!restaurant.getIsSaved()){
                skippedIdList.add(restaurant.getRestaurantID().toString())
            }
        }
        return skippedIdList
    }

    sealed class SavedState {
        object Loading : SavedState()
        object Update : SavedState()
        data class Loaded(var allSaved : MutableList<SavedRestaurants>): SavedState()
    }
}