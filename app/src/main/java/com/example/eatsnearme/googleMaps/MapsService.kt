package com.example.eatsnearme.googleMaps

import android.text.Layout.Directions
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query


private const val BASE_URL = "https://maps.googleapis.com/maps/api/"
const val MAPS_API_KEY = "AIzaSyABfQsj6E4UzPyjyMbTbyrzZTH3LRpPyIU"

interface MapsService {

    @GET("directions/json")
    fun searchPath(
        @Query("key") key: String,
        //LatLng | String | google.maps.Place,
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        // TravelMode Class?
        @Query("travelMode") travelMode: String
    ): Call<DirectionsResponse>

    companion object {
        fun create(): MapsService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(MapsService::class.java)
        }
    }
}