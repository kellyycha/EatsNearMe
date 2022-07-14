package com.example.eatsnearme.restaurants

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import java.util.concurrent.TimeUnit


class LocationService {

    companion object{
        val TIME_LIMIT: Long = TimeUnit.HOURS.toMillis(1L)
        const val TAG = "LocationService"
        const val PERMISSION_REQUEST_CODE = 100
        lateinit var myLocationListener: MyLocationListener
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    interface MyLocationListener {
        fun onLocationChanged(location: Location)
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(activity: Activity, myListener: MyLocationListener) {
        Log.i(TAG,"getting last location")
        myLocationListener = myListener

        LocationListener { location -> myLocationListener.onLocationChanged(location) }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        if (isLocationEnabled(activity)) {
            Log.i(TAG,"location is enabled")
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                val location = task.result
                if (location == null || System.currentTimeMillis() - location.time > TIME_LIMIT) {
                    requestNewLocationData(activity)
                } else {
                    Log.i(TAG,"use last location")
                    myLocationListener.onLocationChanged(location)
                }
            }
        } else {
            Log.i(TAG,"location not enabled")
            Toast.makeText(activity, "please turn on your location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            activity.startActivity(intent)
        }
    }

    private fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData(context: Context){
        Log.i(TAG,"requesting new location")
        fusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_LOW_POWER, null)
            .addOnCompleteListener(OnCompleteListener<Location?> { task ->
                val location = task.result
                if (location == null) {
                    Toast.makeText(context,"unable to get location, please try again later",Toast.LENGTH_LONG).show()
                } else {
                    myLocationListener.onLocationChanged(location)
                }
            })
    }



}