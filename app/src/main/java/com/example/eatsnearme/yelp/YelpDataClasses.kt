package com.example.eatsnearme.yelp

import com.google.gson.annotations.SerializedName

data class YelpSearchResult (
    @SerializedName("total") val total: Int,
    @SerializedName("businesses") val restaurants: List<YelpRestaurant>
)

data class YelpRestaurant(
    val name: String,
    val rating: Double,
    val price: String,
    val review_count: Int,
    val image_url: String,
    @SerializedName("distance") val distance_meters: Double,
    val categories: List<YelpCategory>,
    val location: YelpLocation
)

data class YelpCategory(
    val title: String
)

data class YelpLocation(
    @SerializedName("address1") val address: String
)