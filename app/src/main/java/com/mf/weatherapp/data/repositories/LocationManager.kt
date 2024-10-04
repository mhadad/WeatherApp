package com.mf.weatherapp.data.repositories

import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import android.annotation.SuppressLint
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.mf.weatherapp.data.models.NetworkReqStates
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class LocationManager @Inject constructor(@ApplicationContext private val context: Context) {
//        var currentLocation: MutableStateFlow<Pair<UiStates,Location?>> = MutableStateFlow(Pair(UiStates.EMPTY, null))
var currentLocation: MutableLiveData<Pair<NetworkReqStates,Location?>> = MutableLiveData(Pair(NetworkReqStates.EMPTY, null))
        var fusedLocationClient: FusedLocationProviderClient
init {
    fusedLocationClient = provideFusedLocationProviderClient(this.context )
}
fun provideFusedLocationProviderClient(
     context: Context
): FusedLocationProviderClient
{
    return LocationServices.getFusedLocationProviderClient(context)
}

    @SuppressLint("MissingPermission")
    @Singleton
    fun startLocationUpdates() {
        if(this.fusedLocationClient == null){
            fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context)
        }
            fusedLocationClient.requestLocationUpdates(
                LocationRequest.Builder(Priority.PRIORITY_LOW_POWER, 1000L).build(),
                object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)
                        // Update the location
//                        currentLocation.update{Pair(UiStates.SUCCESS, locationResult.lastLocation)}
//                        currentLocation.value. = Pair(UiStates.SUCCESS, locationResult.lastLocation)
//                        currentLocation = MutableStateFlow(Pair(UiStates.SUCCESS, locationResult.lastLocation))
                        currentLocation.postValue(Pair(NetworkReqStates.SUCCESS, locationResult.lastLocation))

                    }
                },
                Looper.getMainLooper()
            )
        }
//    }
}