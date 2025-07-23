package com.example.myapplication.ui.screens

import android.app.Application
import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)
    private val _location = MutableStateFlow<Location?>(null)
    val location: StateFlow<Location?> = _location

    @SuppressLint("MissingPermission")
    fun fetchLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
            _location.value = loc
        }
    }
}
