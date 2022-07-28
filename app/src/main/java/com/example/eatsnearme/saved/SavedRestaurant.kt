package com.example.eatsnearme.saved

import android.os.Parcelable
import com.example.eatsnearme.parse.SavedRestaurants
import kotlinx.android.parcel.Parcelize


@Parcelize
data class SavedRestaurant (
    val name: String,
    val latitude: Double,
    val longitude: Double) : Parcelable