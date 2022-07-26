package com.example.eatsnearme.saved

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.eatsnearme.R
import com.example.eatsnearme.restaurants.RestaurantsFragment
import com.example.eatsnearme.restaurants.collectLatestLifecycleFlow
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

}

