package com.kellycha.eatsnearme.details

import android.os.Parcelable
import com.kellycha.eatsnearme.parse.SavedRestaurants
import com.kellycha.eatsnearme.yelp.YelpRestaurant
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Restaurant (
    val is_saved: Boolean,
    val name: String,
    val rating: Double,
    val price: String,
    val review_count: Int,
    val image_url: String,
    val categories: String,
    val address: String,
    val coordinates: LatLng,
    val phone: String,
    val url: String) : Parcelable{

    companion object {
        fun from(yelpRestaurant: YelpRestaurant): Restaurant {
            return Restaurant(
                is_saved = false,
                name = yelpRestaurant.name,
                rating = yelpRestaurant.rating,
                price = yelpRestaurant.price.toString(),
                review_count = yelpRestaurant.review_count,
                image_url = yelpRestaurant.image_url,
                categories =  categoriesToString(yelpRestaurant),
                address = yelpRestaurant.location.address,
                coordinates = LatLng(yelpRestaurant.coordinates.latitude.toDouble(),yelpRestaurant.coordinates.longitude.toDouble()),
                phone = yelpRestaurant.phone,
                url = yelpRestaurant.url)
        }
        fun from(parseRestaurant: SavedRestaurants): Restaurant {
            return Restaurant(
                is_saved = true,
                name = parseRestaurant.getRestaurantName().toString(),
                rating = parseRestaurant.getRestaurantRating(),
                price = parseRestaurant.getRestaurantPrice().toString(),
                review_count = parseRestaurant.getRestaurantReviewCount(),
                image_url = parseRestaurant.getRestaurantImage().toString(),
                categories = parseRestaurant.getRestaurantCategories().toString(),
                address = parseRestaurant.getRestaurantAddress().toString(),
                coordinates = LatLng(parseRestaurant.getRestaurantLatitude(),parseRestaurant.getRestaurantLongitude()),
                phone = parseRestaurant.getRestaurantPhone().toString(),
                url = parseRestaurant.getYelpUrl().toString())
        }
        fun categoriesToString(restaurant: YelpRestaurant): String {
            var categories = ""
            for (i in restaurant.categories.indices){
                categories += restaurant.categories[i].title+", "
            }
            return categories.dropLast(2)
        }

    }
}


