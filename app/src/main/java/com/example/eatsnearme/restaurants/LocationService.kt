package com.example.eatsnearme.restaurants

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import java.util.concurrent.TimeUnit

class LocationService {

    companion object{
        private val TIME_LIMIT: Long = TimeUnit.HOURS.toMillis(1L)
        private const val TAG = "LocationService"
        const val PERMISSION_REQUEST_CODE = 100
        private lateinit var myLocationListener: MyLocationListener
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    interface MyLocationListener {
        fun onLocationChanged(location: Location)
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(context: Context, myListener: MyLocationListener) {
        Log.i(TAG,"getting last location")
        myLocationListener = myListener

        LocationListener { location -> myLocationListener.onLocationChanged(location) }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        if (isLocationEnabled(context)) {
            Log.i(TAG,"location is enabled")
            fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                val location = task.result
                if (location == null || System.currentTimeMillis() - location.time > TIME_LIMIT) {
                    requestNewLocationData(context)
                } else {
                    Log.i(TAG,"use last location")
                    myLocationListener.onLocationChanged(location)
                }
            }
        } else {
            Log.i(TAG,"location not enabled")
            Toast.makeText(context, "please turn on your location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            context.startActivity(intent)
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
            .addOnSuccessListener { task ->
                Log.i(TAG,"in oncompletelistener")
                if (task == null) {
                    Log.i(TAG, "unable to get location")
                    Toast.makeText(
                        context,
                        "unable to get location, please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Log.i(TAG, "location: $task")
                    myLocationListener.onLocationChanged(task)
                }
            }
    }

    fun hasPermissions(context: Context): Boolean {
        Log.i(TAG, "Checking Permissions")
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
    }

}