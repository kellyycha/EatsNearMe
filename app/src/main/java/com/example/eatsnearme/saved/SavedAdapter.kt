package com.example.eatsnearme.saved

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.eatsnearme.R
import com.example.eatsnearme.SavedRestaurants
import kotlinx.android.synthetic.main.fragment_saved.*
import kotlinx.android.synthetic.main.item_saved.view.*


class SavedAdapter(val context: Context, private val allSaved: List<SavedRestaurants>) : RecyclerView.Adapter<SavedAdapter.ViewHolder>() {


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

        }

    }

}