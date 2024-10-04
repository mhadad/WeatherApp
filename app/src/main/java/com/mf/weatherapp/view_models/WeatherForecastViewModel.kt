package com.mf.weatherapp.view_models

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mf.weatherapp.data.models.NetworkReqStates
import com.mf.weatherapp.data.models.ReverseGeocodingLocation
import com.mf.weatherapp.data.models.UiStates
import com.mf.weatherapp.data.models.WeatherForecast
import com.mf.weatherapp.data.repositories.LocationManager
import com.mf.weatherapp.data.repositories.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class WeatherForecastViewModel @Inject constructor( var restAPICaller: ApiService, var locationManager: LocationManager ) : ViewModel() {
    var appID: String = "da33f5713dbaf998ae0fd094c401a24d"
    var weatherForecastState : MutableLiveData<Pair<UiStates, WeatherForecast?>> = MutableLiveData(Pair(UiStates(NetworkReqStates.EMPTY, null), null))

//    var reverseGeocodingState: MutableStateFlow<Pair<UiStates, ReverseGeocodingLocation?>>  = MutableStateFlow(Pair(UiStates(NetworkReqStates.EMPTY, null), null))
    var reverseGeocodingState: MutableLiveData<Pair<UiStates, ReverseGeocodingLocation?>>  = MutableLiveData(Pair(UiStates(NetworkReqStates.EMPTY, null), null))
//    var currentLocation: StateFlow<Pair<UiStates, Location?>> = MutableStateFlow(Pair(UiStates.EMPTY,null)).stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList())
//var currentLocation: StateFlow<Pair<UiStates, Location?>> = LocationManager(context).currentLocation.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = Pair(UiStates.EMPTY, null))
//    var currentLocation: StateFlow<Pair<UiStates, Location?>> = locationManager.currentLocation.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = Pair(UiStates.EMPTY, null))
//    var currentLocation: MutableStateFlow<Pair<UiStates, Location?>> = locationManager.currentLocation
    val currentLocationObserver : Observer<Pair<NetworkReqStates, Location? >> = Observer{ updatedData->
//        getWeatherForecastByCoordinates(updatedData?.second?.latitude, updatedData.second.longitude?.toLong())
        if(updatedData.second != null) {
            getWeatherForecastByCoordinates(
                updatedData?.second?.latitude?.toLong()!!,
                updatedData.second?.longitude?.toLong()!!,
                appID
            )
            getLocationDataByCoordinates(updatedData?.second?.latitude?.toLong()!!,
                updatedData.second?.longitude?.toLong()!!,
                appID)
            Log.d("Data", updatedData.toString())
        }

    }
    val geocodingLocationObserver: Observer<Pair<UiStates, ReverseGeocodingLocation?>> = Observer{ updatedData ->
        if(updatedData.second != null) {
            if(updatedData?.second != null) {
                getWeatherForecastByCoordinates(
                    updatedData?.second?.lat?.toLong()!!,
                    updatedData.second?.lon?.toLong()!!,
                    appID
                )
                Log.d("Data", updatedData.toString())
            }
        }
    }
    init {
        locationManager.currentLocation.observeForever(currentLocationObserver)
        reverseGeocodingState.observeForever(geocodingLocationObserver)
    }

//    init {
//        @Inject
//        locationManager = LocationManager()
//    }
//init {
//    currentLocation.observe(this@WeatherForecastViewModel, Observer {
//
//    })
//}
    fun getWeatherForecastByCoordinates(latitude: Long, longitude: Long, appID: String){
        viewModelScope.launch(Dispatchers.IO) {
            setLoadingState()
            val weatherForecastRes = restAPICaller.getWeatherByCoordinates(latitude, longitude, appID)
            if(weatherForecastRes.isSuccessful){
//                weatherForecast = MutableStateFlow(Pair(UiStates.ERROR, weatherForecastRes.body()))
//                weatherForecastState = MutableStateFlow(Pair(UiStates(NetworkReqStates.SUCCESS, weatherForecastRes.errorBody().toString()), weatherForecastRes.body()))
                weatherForecastState.postValue(Pair(UiStates(NetworkReqStates.SUCCESS, weatherForecastRes.message().toString()), weatherForecastRes.body()))
            }
            else if(weatherForecastRes.errorBody() != null){
//            weatherForecastState = MutableStateFlow(Pair(UiStates(NetworkReqStates.ERROR, weatherForecastRes.message()), null))
                weatherForecastState.postValue(Pair(UiStates(NetworkReqStates.ERROR, weatherForecastRes.message()), null))
//
            }
        }
    }

    fun getLocation(){
        viewModelScope.launch(Dispatchers.IO) {
            setLoadingState()
            locationManager.startLocationUpdates()
        }
    }

    fun getLocationDataByCoordinates(latitude: Long, longitude: Long, appID: String){
        viewModelScope.launch(Dispatchers.IO) {
            val locationDataRes = restAPICaller.getLocationDataByCoordinates(latitude, longitude,1, appID)
            if(locationDataRes.isSuccessful){
                reverseGeocodingState.postValue(Pair(UiStates(NetworkReqStates.SUCCESS, null), locationDataRes.body()?.get(0)))
            }
            else if(locationDataRes.errorBody() != null){
                reverseGeocodingState.postValue(Pair(UiStates(NetworkReqStates.ERROR, locationDataRes.message()), null))
            }
        }
    }

    fun getLocationDataByZipCode(zipCode: String){
        var zipCode_countryCode = "${zipCode},US"
        viewModelScope.launch(Dispatchers.IO)
        {
            val locationDataRes = restAPICaller.getLocationDataByZipCode(zipCode_countryCode, appID)
            if(locationDataRes.isSuccessful){
                reverseGeocodingState.postValue(Pair(UiStates(NetworkReqStates.SUCCESS, locationDataRes.errorBody().toString()), locationDataRes.body()))
            }
            else if(locationDataRes.errorBody() != null){
                reverseGeocodingState.postValue(Pair(UiStates(NetworkReqStates.ERROR, locationDataRes.message()), null))
            }
        }
    }
    suspend fun executeLocationDataQuery(searchQuery:String){
        if(searchQuery.count { it == ',' }>1) {
            // cityName, StateCode and CountryCode
            getLocationDataByCityName_StateCode_CountryCode(searchQuery)
        }
        else if(searchQuery.count { it == ',' }==1) {
            // cityName,CountryCode
            getLocationDataByCityName_CountryCode(cityName_CountryCode = searchQuery)
        }
        else if(searchQuery.count { it==',' } == 0 && searchQuery.contains("^[A-Za-z\\s]+$".toRegex())) {
            // cityName query only
            getLocationDataByCityName(searchQuery)
        }
        else if(searchQuery.count { it==',' } == 0 && searchQuery.contains("^[0-9]+$".toRegex())){
            // zipCode lookup
            getLocationDataByZipCode(searchQuery)
        }
    }

    private suspend fun getLocationDataByCityName(cityName: String){
        viewModelScope.launch(Dispatchers.IO)
        {
            setLoadingState()
            val weatherForecastRes = restAPICaller.getWeatherByCityName(cityName, appID)
            postWeaterDataValue(weatherForecastRes)
        }
    }

    private suspend fun getLocationDataByCityName_CountryCode(cityName_CountryCode: String) {
        viewModelScope.launch(Dispatchers.IO)
        {
            setLoadingState()
            val weatherForecastRes =
                restAPICaller.getWeatherByCityName_CountryCode(cityName_CountryCode, appID)

            postWeaterDataValue(weatherForecastRes)
        }
    }
    private suspend fun getLocationDataByCityName_StateCode_CountryCode(cityName_CountryCode: String) {
        viewModelScope.launch(Dispatchers.IO)
        {
            setLoadingState()
            val weatherForecastRes =
                restAPICaller.getWeatherByCityName_StateCode_CountryCode(cityName_CountryCode, appID)
            postWeaterDataValue(weatherForecastRes)
        }
    }
    private fun setLoadingState(){
        weatherForecastState.postValue(
            Pair(
                UiStates(
                    NetworkReqStates.LOADING,
                    null
                ), null
            )
        )
    }
    private fun postWeaterDataValue(weatherForecastRes: Response<WeatherForecast?>){
        if (weatherForecastRes.isSuccessful) {
//                weatherForecast = MutableStateFlow(Pair(UiStates.ERROR, weatherForecastRes.body()))
//                weatherForecastState = MutableStateFlow(Pair(UiStates(NetworkReqStates.SUCCESS, weatherForecastRes.errorBody().toString()), weatherForecastRes.body()))
            weatherForecastState.postValue(
                Pair(
                    UiStates(
                        NetworkReqStates.SUCCESS,
                        weatherForecastRes.message().toString()
                    ), weatherForecastRes.body()
                )
            )
            val weatherForecastObj = weatherForecastRes.body()
            if(weatherForecastObj != null)
                reverseGeocodingState.postValue(Pair(UiStates(NetworkReqStates.SUCCESS, null),ReverseGeocodingLocation(weatherForecastObj?.name!!, null, null, weatherForecastObj?.sys?.country!!, null)))
        } else if (weatherForecastRes.errorBody() != null) {
//            weatherForecastState = MutableStateFlow(Pair(UiStates(NetworkReqStates.ERROR, weatherForecastRes.message()), null))
            weatherForecastState.postValue(
                Pair(
                    UiStates(
                        NetworkReqStates.ERROR,
                        weatherForecastRes.message()
                    ), null
                )
            )
//
        }
    }

}