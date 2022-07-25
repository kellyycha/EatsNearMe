package com.example.eatsnearme.restaurants

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.eatsnearme.R
import com.example.eatsnearme.yelp.YelpRestaurant


class CardAdapter(context: Context?, resourceId: Int, items: List<YelpRestaurant>?) : ArrayAdapter<YelpRestaurant?>(
    context!!, resourceId, items as List<YelpRestaurant?>
) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var convertView = convertView
        val cardItem: YelpRestaurant? = getItem(position)
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false)
        }

        val tvName = convertView?.findViewById<TextView>(R.id.tvName)
        val tvPrice = convertView?.findViewById<TextView>(R.id.tvPrice)
        val ratingBar = convertView?.findViewById<RatingBar>(R.id.ratingBar)
        val ivYelpPic = convertView?.findViewById<ImageView>(R.id.ivYelpPic)

        tvName!!.text = cardItem!!.name
        tvPrice!!.text = cardItem.price
        ratingBar!!.rating = cardItem.rating.toFloat()
        Glide.with(convertView!!.context).load(cardItem.image_url).into(ivYelpPic!!)

        return convertView
    }
}