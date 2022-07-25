package com.example.eatsnearme.details

import android.os.Parcelable
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
    val is_open_now: Boolean) : Parcelable
