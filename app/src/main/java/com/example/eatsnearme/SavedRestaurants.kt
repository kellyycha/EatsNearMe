package com.example.eatsnearme

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseUser

const val KEY_USER = "user"
const val KEY_RESTAURANT = "restaurant"

@ParseClassName("SavedRestaurants")
class SavedRestaurants : ParseObject() {

    fun getUser(): ParseUser? {
        return getParseUser(KEY_USER)
    }

    fun setUser(user: ParseUser?) {
        put(KEY_USER, user!!)
    }

    fun getRestaurantName(): String? {
        return getString(KEY_RESTAURANT)
    }

    fun setRestaurantName(restaurant: String?) {
        put(KEY_RESTAURANT, restaurant!!)
    }

//    fun getAllSaved(user: ParseUser) : ArrayList<String> {
//        //TODO: get list of saved restaurants per user here?
//    }
    
}