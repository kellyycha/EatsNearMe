package com.kellycha.eatsnearme.parse

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseUser

const val KEY_ID = "yelpId"
const val KEY_IS_SAVED = "saved"
const val KEY_USER = "user"
const val KEY_NAME = "name"
const val KEY_RATING = "rating"
const val KEY_PRICE = "price"
const val KEY_REVIEW_COUNT = "reviewCount"
const val KEY_IMAGE_URL = "imageUrl"
const val KEY_CATEGORIES = "categories"
const val KEY_ADDRESS = "address"
const val KEY_PHONE = "phone"
const val KEY_LATITUDE = "latitude"
const val KEY_LONGITUDE = "longitude"
const val KEY_URL = "yelpUrl"

@ParseClassName("SavedRestaurants")
class SavedRestaurants : ParseObject() {

    fun getRestaurantID(): String? {
        return getString(KEY_ID)
    }
    fun setRestaurantID(yelpId: String) {
        put(KEY_ID, yelpId)
    }

    fun getIsSaved(): Boolean {
        return getBoolean(KEY_IS_SAVED)
    }
    fun setIsSaved(isSaved: Boolean){
        put(KEY_IS_SAVED, isSaved)
    }

    fun getUser(): ParseUser? {
        return getParseUser(KEY_USER)
    }
    fun setUser(user: ParseUser) {
        put(KEY_USER, user)
    }

    fun getRestaurantName(): String? {
        return getString(KEY_NAME)
    }
    fun setRestaurantName(name: String) {
        put(KEY_NAME, name)
    }

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
        price?.let { p -> put(KEY_PRICE, p) }
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
        imageUrl?.let { url -> put(KEY_IMAGE_URL, url) }
    }

    fun getRestaurantAddress(): String? {
        return getString(KEY_ADDRESS)
    }
    fun setRestaurantAddress(address: String) {
        put(KEY_ADDRESS, address)
    }

    fun getRestaurantCategories(): String? {
        return getString(KEY_CATEGORIES)
    }
    fun setRestaurantCategories(categories: String?) {
        categories?.let { c -> put(KEY_CATEGORIES, c) }
    }

    fun getRestaurantPhone(): String? {
        return getString(KEY_PHONE)
    }
    fun setRestaurantPhone(phone: String?) {
        phone?.let { p -> put(KEY_PHONE, p) }
    }

    fun getRestaurantLatitude(): Double {
        return getDouble(KEY_LATITUDE)
    }
    fun setRestaurantLatitude(latitude: Double) {
        put(KEY_LATITUDE, latitude)
    }

    fun getRestaurantLongitude(): Double {
        return getDouble(KEY_LONGITUDE)
    }
    fun setRestaurantLongitude(longitude: Double) {
        put(KEY_LONGITUDE, longitude)
    }

    fun getYelpUrl(): String? {
        return getString(KEY_URL)
    }
    fun setYelpUrl(url: String?) {
        url?.let { u -> put(KEY_URL, u) }
    }

}