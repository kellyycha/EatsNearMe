package com.kellycha.eatsnearme.yelp

import com.google.gson.annotations.SerializedName

data class YelpSearchResult(
    @SerializedName("total") val total: Int,
    @SerializedName("businesses") val restaurants: List<YelpRestaurant>
)

data class YelpRestaurant(
    var id: String,
    var name: String,
    val rating: Double,
    val price: String?,
    val review_count: Int,
    val image_url: String,
    val categories: List<YelpCategory>,
    val location: YelpLocation,
    val coordinates: YelpCoordinates,
    @SerializedName("display_phone") val phone: String,
    val url: String
)

data class YelpCategory(
    val title: String
)

data class YelpLocation(
    @SerializedName("address1") val address: String
)

data class YelpCoordinates(
    val latitude: Float,
    val longitude: Float
)