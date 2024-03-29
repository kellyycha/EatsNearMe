package com.kellycha.eatsnearme.saved

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kellycha.eatsnearme.R
import com.kellycha.eatsnearme.parse.SavedRestaurants
import com.kellycha.eatsnearme.details.DetailsFragment
import com.kellycha.eatsnearme.details.Restaurant
import kotlinx.android.synthetic.main.item_saved.view.*

class SavedAdapter(val context: Context, private val allSaved: MutableList<SavedRestaurants>) : RecyclerView.Adapter<SavedAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_saved, parent, false))
    }

    override fun getItemCount() = allSaved.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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
                goToDetailsView(saved)
            }
        }

        private fun goToDetailsView(saved: SavedRestaurants) {
            val currentRestaurant = Restaurant.from(saved)
            val fragment = DetailsFragment.newInstance(currentRestaurant)
            val activity = itemView.context as AppCompatActivity
            val transaction = activity.supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up, R.anim.slide_out_down, R.anim.slide_in_down)
            transaction.replace(R.id.flContainer, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }

}