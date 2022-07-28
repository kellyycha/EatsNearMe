package com.example.eatsnearme.details

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import com.example.eatsnearme.restaurants.LocationService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val _currLocation = MutableStateFlow<CurrLocationState>(CurrLocationState.Loading)
    val currLocation: StateFlow<CurrLocationState> = _currLocation

    fun getCurrentLocation() {
        _currLocation.value = CurrLocationState.Loading
        LocationService().getLastLocation(getApplication(), object : LocationService.MyLocationListener{
            override fun onLocationChanged(location: Location) {
                var currLocation = LatLng(location.latitude, location.longitude)
                _currLocation.value = CurrLocationState.Loaded(currLocation)
            }
        })
    }

    sealed class CurrLocationState {
        object Loading : CurrLocationState()
        data class Loaded(var coordinates : LatLng): CurrLocationState()
    }
}