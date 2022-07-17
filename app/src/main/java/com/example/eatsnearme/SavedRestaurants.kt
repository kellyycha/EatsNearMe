package com.example.eatsnearme

import com.example.eatsnearme.yelp.YelpCategory
import com.example.eatsnearme.yelp.YelpLocation
import com.example.eatsnearme.yelp.YelpRestaurant
import com.google.gson.annotations.SerializedName
import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseUser
import org.json.JSONObject

const val KEY_IS_SAVED = "saved"
const val KEY_USER = "user"
const val KEY_NAME = "name"
const val KEY_RATING = "rating"
const val KEY_PRICE = "price"
const val KEY_REVIEW_COUNT = "reviewCount"
const val KEY_IMAGE_URL = "imageUrl"
const val KEY_DISTANCE_METERS = "distance"
const val KEY_CATEGORIES = "categories"
const val KEY_LOCATION = "location"


@ParseClassName("SavedRestaurants")
class SavedRestaurants : ParseObject() {

    fun getIsSaved(): Boolean? {
        return getBoolean(KEY_IS_SAVED)
    }

    fun setIsSaved(isSaved: Boolean?){
        put(KEY_IS_SAVED, isSaved!!)
    }

    fun getUser(): ParseUser? {
        return getParseUser(KEY_USER)
    }

    fun setUser(user: ParseUser?) {
        put(KEY_USER, user!!)
    }

    fun getRestaurantName(): String? {
        return getString(KEY_NAME)
    }

    fun setRestaurantName(name: String?) {
        put(KEY_NAME, name!!)
    }


    //TODO: do I need all these fields? Maybe for displaying info in the saved fragments screen

    fun getRestaurantRating(): Double {
        return getDouble(KEY_RATING)
    }

    fun setRestaurantRating(rating: Double) {
        put(KEY_RATING, rating)
    }

    fun getRestaurantPrice(): String? {
        return getString(KEY_PRICE)
    }

    fun setRestaurantPrice(price: String?) {
        put(KEY_PRICE, price!!)
    }

    fun getRestaurantReviewCount(): Int {
        return getInt(KEY_REVIEW_COUNT)
    }

    fun setRestaurantReviewCount(reviewCount: Int) {
        put(KEY_REVIEW_COUNT, reviewCount)
    }


    fun getRestaurantImage(): String? {
        return getString(KEY_IMAGE_URL)
    }

    fun setRestaurantImage(imageUrl: String?) {
        put(KEY_IMAGE_URL, imageUrl!!)
    }

    fun getRestaurantDistance(): Double {
        return getDouble(KEY_DISTANCE_METERS)
    }

    fun setRestaurantDistance(distance: Double) {
        put(KEY_DISTANCE_METERS, distance)
    }

    // TODO: How to add fields for categories or location?

//val categories: List<YelpCategory>,
//val location: YelpLocation
}