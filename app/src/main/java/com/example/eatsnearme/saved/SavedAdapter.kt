package com.example.eatsnearme.saved

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eatsnearme.R
import com.example.eatsnearme.SavedRestaurants
import com.example.eatsnearme.details.DetailsFragment
import com.example.eatsnearme.details.Restaurant
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.item_saved.view.*


class SavedAdapter(val context: Context, private val allSaved: List<SavedRestaurants>) : RecyclerView.Adapter<SavedAdapter.ViewHolder>() {

    companion object{
        const val TAG = "Saved"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_saved, parent, false))
    }

    override fun getItemCount() = allSaved.size

    override fun onBindViewHolder(holder: SavedAdapter.ViewHolder, position: Int) {
        val saved = allSaved[position]
        holder.bind(saved)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(saved: SavedRestaurants) {

            itemView.tvSavedName.text = saved.getRestaurantName()
            itemView.tvSavedAddress.text = saved.getRestaurantAddress()
            itemView.tvCategories.text = saved.getRestaurantCategories()
            itemView.tvPrice.text = saved.getRestaurantPrice()
            Glide.with(context).load(saved.getRestaurantImage()).into(itemView.ivPic1)

            itemView.setOnClickListener{
                Log.i(TAG,"clicked ${saved.getRestaurantName()}")
                val bundle = Bundle()
                // TODO: get rid of !! later. just trying to see if this works.
                val currentRestaurant = Restaurant(true,
                    saved.getRestaurantName()!!,
                    saved.getRestaurantRating(),
                    saved.getRestaurantPrice()!!,
                    saved.getRestaurantReviewCount(),
                    saved.getRestaurantImage()!!,
                    saved.getRestaurantCategories()!!,
                    saved.getRestaurantAddress()!!,
                    LatLng(saved.getRestaurantLatitude(),saved.getRestaurantLongitude()),
                    saved.getRestaurantPhone()!!,
                    saved.getIsOpened())
                bundle.putParcelable("Restaurant", currentRestaurant)

                val fragment = DetailsFragment()
                fragment.arguments = bundle

                val activity = itemView.context as AppCompatActivity
                val transaction = activity.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.flContainer, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }

    }

}