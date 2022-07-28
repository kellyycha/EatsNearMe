package com.example.eatsnearme.saved

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatsnearme.R
import com.example.eatsnearme.details.DetailsFragment
import com.example.eatsnearme.details.Restaurant
import com.example.eatsnearme.parse.SavedRestaurants
import com.example.eatsnearme.restaurants.RestaurantsFragment
import com.example.eatsnearme.restaurants.collectLatestLifecycleFlow
import com.example.eatsnearme.saved.SavedMapFragment.Companion.KEY_MAP
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

        viewModel.querySaved()

        collectLatestLifecycleFlow(viewModel.stateFlow) {
            when(it){
                is SavedViewModel.SavedState.Loading -> {
                    Log.i(RestaurantsFragment.TAG, "Loading restaurants")
                    savedSpinner.visibility = View.VISIBLE
                }
                is SavedViewModel.SavedState.Update -> {
                    Log.i(RestaurantsFragment.TAG, "Deleted a card, show updated list")
                }
                is SavedViewModel.SavedState.Loaded -> {
                    Log.i(RestaurantsFragment.TAG, "Loaded all")
                    savedSpinner.visibility = View.GONE

                    val adapter = SavedAdapter(requireActivity(), it.allSaved)
                    rvSaved.adapter = adapter
                    rvSaved.layoutManager = LinearLayoutManager(requireContext())

                    btnMap.setOnClickListener{ _ ->
                        Log.i("button", "map clicked")
                        gotoMapView(it.allSaved)
                    }

                    val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
                        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                            viewModel.removeItemAt(viewHolder.adapterPosition)
                            adapter.notifyItemRemoved(viewHolder.adapterPosition)
                        }
                    }
                    val itemTouchHelper = ItemTouchHelper(swipeHandler)
                    itemTouchHelper.attachToRecyclerView(rvSaved)

                    adapter.notifyDataSetChanged()

                }
            }
        }
    }

    private fun gotoMapView(allSaved: MutableList<SavedRestaurants>) {

        val savedList = ArrayList<Restaurant>()
        for (saved in allSaved){
            val currentRestaurant = Restaurant.from(saved)
            savedList.add(currentRestaurant)
        }
        val fragment = SavedMapFragment.newInstance(savedList)
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
//        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_out_down, R.anim.slide_in_down)
        transaction.replace(R.id.flContainer, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}

