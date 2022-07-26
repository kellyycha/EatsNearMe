package com.example.eatsnearme.details

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import com.example.eatsnearme.restaurants.LocationService
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DetailsViewModel(application: Application) : AndroidViewModel(application) {

    private val _stateFlow = MutableStateFlow<DetailState>(DetailState.Loading)
    val stateFlow: StateFlow<DetailState> = _stateFlow

    fun getCurrentLocation() {
        _stateFlow.value = DetailState.Loading
        LocationService().getLastLocation(getApplication(), object : LocationService.MyLocationListener{
            override fun onLocationChanged(location: Location) {
                var currLocation = LatLng(location.latitude, location.longitude)
                _stateFlow.value = DetailState.Loaded(currLocation)
            }
        })
    }

    sealed class DetailState {
        object Loading : DetailState()
        data class Loaded(var currLocation : LatLng): DetailState()
    }

}