package com.example.eatsnearme.saved

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eatsnearme.KEY_USER
import com.example.eatsnearme.R
import com.example.eatsnearme.SavedRestaurants
import com.parse.FindCallback
import com.parse.ParseQuery
import com.parse.ParseUser
import kotlinx.android.synthetic.main.fragment_saved.*

const val TAG = "SavedFragment"
private const val QUERY_LIMIT = 20

open class SavedFragment : Fragment() {
    //val savedNamesList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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
        val query: ParseQuery<SavedRestaurants> = ParseQuery.getQuery(SavedRestaurants::class.java)
        query.include(KEY_USER)
            .whereEqualTo(KEY_USER, ParseUser.getCurrentUser())
            .setLimit(QUERY_LIMIT)
            .addDescendingOrder("createdAt")
            .findInBackground(FindCallback<SavedRestaurants> { restaurants, e -> // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting saved restaurants", e)
                    return@FindCallback;
                }
                for (restaurant in restaurants) {
                    Log.i(TAG,"Restaurant: " + restaurant.getRestaurantName().toString())
                    //savedNamesList.add(restaurant.getRestaurantName().toString())
                }

                // save received posts to list and notify adapter of new data
                allSaved.addAll(restaurants)
                adapter.notifyDataSetChanged()
                Log.i(TAG, "notified with restaurants: $restaurants")
            })
    }

//    @JvmName("getSavedNamesList1")
//    fun getSavedNamesList(): ArrayList<String> {
//        return savedNamesList
//
//    }


}
