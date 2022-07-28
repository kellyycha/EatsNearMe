package com.example.eatsnearme.yelp

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

private const val BASE_URL = "https://api.yelp.com/v3/"
const val YELP_API_KEY = "s_Q0zANLiLbbp8ga5gCjJ7K-MiwmTnqTIUzX9XFnBvYvFE7iN7nMDUf7e5a6JnC9CVLPEBTfyE1zVwV-Y3zfl9IadSiIvUFyhXHnILfz7_Gt6CjscNONxY6jAwOxYnYx"

interface YelpService {

    @GET("businesses/search")
    fun searchRestaurants(
        @Header("Authorization") authHeader: String,
        @Query("term") searchTerm: String,
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("limit") limit: Int,
        @Query ("price") price: String,
        @Query ("open_now") open_now: Boolean) : Call<YelpSearchResult>

    companion object {
        fun create(): YelpService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(YelpService::class.java)
        }
    }
}