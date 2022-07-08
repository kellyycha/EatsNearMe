package com.example.eatsnearme.restaurants

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.eatsnearme.MainActivity
import com.example.eatsnearme.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationService : AppCompatActivity() {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // hardcoded to test
    private var currLatitude = "37.48075357532599"
    private var currLongitude = "-122.1654402366265"

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
        private const val TAG = "LocationService"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile)
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)

        getCurrentLocation()
    }

    fun getCurrentLocation() {
        if(checkPermissions()) {
            if(isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        Log.i(TAG, "Null Received")
                    }
                    else {
                        Log.i(TAG, "Get Successful")

                        currLatitude = location.latitude.toString()
                        currLongitude = location.longitude.toString()
                        Log.i(TAG, "Latitude: $currLatitude")
                        Log.i(TAG, "Longitude: $currLongitude}")
                    }
                }
            }
            else{
                Log.i(TAG, "Turn on Location")
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        else{
            Log.i(TAG, "Request Permission")
            requestPermission()
        }
    }

    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSION_REQUEST_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.i(TAG, "Permission Granted")
                getCurrentLocation()
            }
            else {
                Log.i(TAG, "Permission Denied")
            }
        }
    }

    fun getCoordinates() : String {
        return "$currLatitude, $currLongitude"
    }

}