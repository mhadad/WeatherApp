package com.mf.weatherapp.view_models

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mf.weatherapp.data.dao.SearchQueriesDAO
import com.mf.weatherapp.data.models.NetworkReqStates
import com.mf.weatherapp.data.models.ReverseGeocodingLocation
import com.mf.weatherapp.data.models.SearchQueries
import com.mf.weatherapp.data.models.UiStates
import com.mf.weatherapp.data.models.WeatherForecast
import com.mf.weatherapp.data.repositories.LocationManager
import com.mf.weatherapp.data.repositories.network.ApiService
import com.mf.weatherapp.data.repositories.network.ApiService_Kotlin
import com.mf.weatherapp.data.repositories.network.RetrofitInstance
import com.mf.weatherapp.data.repositories.network.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class WeatherForecastViewModel @Inject constructor(val restAPICaller: ApiService_Kotlin, val locationManager: LocationManager, val searchQueriesDAO: SearchQueriesDAO, application: Application ) : AndroidViewModel(application) {
    var searchQueries: StateFlow<List<SearchQueries>> = searchQueriesDAO.getAllSearchQueries().stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList())
    var appID: String = "da33f5713dbaf998ae0fd094c401a24d"
    var _weatherForecastState : MutableStateFlow<Pair<UiStates, WeatherForecast?>> = MutableStateFlow(Pair(UiStates(NetworkReqStates.EMPTY, null), null))
    var weatherForecastState : StateFlow<Pair<UiStates, WeatherForecast?>> = _weatherForecastState.asStateFlow()
    var weatherForecastStateSharedFlow : SharedFlow<Pair<UiStates, WeatherForecast?>> = _weatherForecastState.asSharedFlow()
    var reverseGeocodingState: MutableLiveData<Pair<UiStates, ReverseGeocodingLocation?>>  = MutableLiveData(Pair(UiStates(NetworkReqStates.EMPTY, null), null))
    var context = getApplication<Application>()
    val currentLocationObserver : Observer<Pair<NetworkReqStates, Location? >> = Observer{ updatedData->
        updatedData.second?.let {
            getWeatherForecastByCoordinates(
                it.latitude.toLong(),
                it.longitude.toLong(),
                appID
            )
            getLocationDataByCoordinates(
                it.latitude.toLong(),
                it.longitude.toLong(),
                appID)
        }
    }
    val geocodingLocationObserver: Observer<Pair<UiStates, ReverseGeocodingLocation?>> = Observer{ updatedData ->
        updatedData.second?.let { updatedDataLet ->
            updatedDataLet.lat?.let { lat ->
                getWeatherForecastByCoordinates(
                    lat.toLong(),
                    updatedDataLet.lon?.toLong()!!,
                    appID
                )
            }
        }
    }
    init {

        locationManager.currentLocation.observeForever(currentLocationObserver)
        reverseGeocodingState.observeForever(geocodingLocationObserver)

    }



    fun getWeatherForecastByCoordinates(latitude: Long, longitude: Long, appID: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                setLoadingState()
                if(Utils.isNetworkAvailable(this@WeatherForecastViewModel.context)) {
                    val weatherForecastRes =
                        restAPICaller.getWeatherByCoordinates(latitude, longitude, appID = appID)
                    if (weatherForecastRes.isSuccessful) {
                        _weatherForecastState.emit(
                            Pair(
                                UiStates(
                                    NetworkReqStates.SUCCESS,
                                    weatherForecastRes.message().toString()
                                ), weatherForecastRes.body()
                            )
                        )
                    } else if (weatherForecastRes.errorBody() != null) {
                        _weatherForecastState.emit(
                            Pair(
                                UiStates(
                                    NetworkReqStates.ERROR,
                                    weatherForecastRes.message()
                                ), null
                            )
                        )
                    }
                }
                else {
                    throw IOException("Network Unreachable")
                }
            }
            catch (e: Exception){
                e.message?.let {
                    Log.e("Exception", it)
                    postWeatherForecastRequestsError(it)
                    cancelAllCoroutines(it)
                }

            }
        }
    }

    fun getLocation(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                setLoadingState()
                locationManager.startLocationUpdates()
            }
            catch(e: Exception){
                e.message?.let {
                    Log.e("Exception", it)
                    postGeocodingError(it)
                    cancelAllCoroutines(it)
                }

            }
        }
    }

    fun getLocationDataByCoordinates(latitude: Long, longitude: Long, appID: String){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                setLoadingState()
                if(Utils.isNetworkAvailable(this@WeatherForecastViewModel.context)) {
                    val locationDataRes = restAPICaller.getLocationDataByCoordinates(
                        latitude,
                        longitude,
                        1,
                        appID = appID
                    )
                    if (locationDataRes.isSuccessful) {
                        reverseGeocodingState.postValue(
                            Pair(
                                UiStates(NetworkReqStates.SUCCESS, null),
                                locationDataRes.body()?.get(0)
                            )
                        )
                    } else if (locationDataRes.errorBody() != null) {
                        reverseGeocodingState.postValue(
                            Pair(
                                UiStates(
                                    NetworkReqStates.ERROR,
                                    locationDataRes.message()
                                ), null
                            )
                        )
                    }
                }
                else{
                    throw IOException("Network Unreachable")
                }
            }
            catch (e: Exception){
                e.message?.let {
                    Log.e("Exception", it)
                    postGeocodingError(it)
                    cancelAllCoroutines(it)
                }
            }
        }
    }

    fun getLocationDataByZipCode(zipCode: String){
        var zipCode_countryCode = "${zipCode},US"
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                setLoadingState()
                if(Utils.isNetworkAvailable(this@WeatherForecastViewModel.context)) {
                    val locationDataRes =
                        restAPICaller.getLocationDataByZipCode(zipCode_countryCode, appID = appID)
                    if (locationDataRes.isSuccessful) {
                        reverseGeocodingState.postValue(
                            Pair(
                                UiStates(
                                    NetworkReqStates.SUCCESS,
                                    locationDataRes.errorBody().toString()
                                ), locationDataRes.body()
                            )
                        )
                    } else if (locationDataRes.errorBody() != null) {
                        reverseGeocodingState.postValue(
                            Pair(
                                UiStates(
                                    NetworkReqStates.ERROR,
                                    locationDataRes.message()
                                ), null
                            )
                        )
                    }
                }
                else {
                    throw IOException("Network Unreachable")
                }
            }
            catch (e: Exception){
                    Log.e("Exception", e.message!! )
                    e.message?.let {
                        Log.e("Exception", it)
                        postGeocodingError(it)
                        cancelAllCoroutines(it)
                    }
                }
        }
    }
    private fun postGeocodingError(message: String){
        reverseGeocodingState.postValue(Pair(UiStates(NetworkReqStates.ERROR, message), null))
    }
    suspend fun executeLocationDataQuery(searchQuery:String){
        Log.d("Data", searchQuery)
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
            try {
                setLoadingState()
                if(Utils.isNetworkAvailable(this@WeatherForecastViewModel.context)) {
                    val weatherForecastRes =
                        restAPICaller.getWeatherByCityName(cityName, appID = appID)
                    postWeaterDataValue(weatherForecastRes)
                }
                else
                    throw IOException("Network Unreachable")
            }
            catch (e: Exception){
                e.message?.let {
                    Log.e("Exception", it)
                    postWeatherForecastRequestsError(it)
                    cancelAllCoroutines(it)
                }
            }
        }
    }

    private suspend fun getLocationDataByCityName_CountryCode(cityName_CountryCode: String) {
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                setLoadingState()
                if(Utils.isNetworkAvailable(context)) {
                    val weatherForecastRes =
                        restAPICaller.getWeatherByCityName_CountryCode(
                            cityName_CountryCode,
                            appID = appID
                        )

                    postWeaterDataValue(weatherForecastRes)
                }
                else {
                    throw IOException("Network Unreachable")
                }
            }
            catch (e: Exception){
                e.message?.let {
                    Log.e("Exception", it)
                    postWeatherForecastRequestsError(it)
                    cancelAllCoroutines(it)
                }
            }
        }
    }
    private suspend fun getLocationDataByCityName_StateCode_CountryCode(cityName_CountryCode: String) {
        viewModelScope.launch(Dispatchers.IO)
        {
            try {
                setLoadingState()
                if(Utils.isNetworkAvailable(this@WeatherForecastViewModel.context)){
                val weatherForecastRes =
                    restAPICaller.getWeatherByCityName_StateCode_CountryCode(
                        cityName_CountryCode,
                        appID = appID
                    )
                postWeaterDataValue(weatherForecastRes)
                }
                else{
                    throw IOException("Netowrk unreachable")
                }
            }
            catch (e: Exception){
                e.message?.let {
                    Log.e("Exception", it)
                    postWeatherForecastRequestsError(it)
                    cancelAllCoroutines(it)
                }
            }
        }
    }
    private suspend fun postWeatherForecastRequestsError(message: String){
        _weatherForecastState.emit(Pair(UiStates(NetworkReqStates.ERROR, message), null))
    }
    private suspend fun setLoadingState(){
        _weatherForecastState.emit(
            Pair(
                UiStates(
                    NetworkReqStates.LOADING,
                    null
                ), null
            )
        )
    }
    private suspend fun postWeaterDataValue(weatherForecastRes: Response<WeatherForecast?>){
        if (weatherForecastRes.isSuccessful) {
            _weatherForecastState.emit(
                Pair(
                    UiStates(
                        NetworkReqStates.SUCCESS,
                        weatherForecastRes.message().toString()
                    ), weatherForecastRes.body()
                )
            )
            val weatherForecastObj = weatherForecastRes.body()
            if(weatherForecastObj != null)
                reverseGeocodingState.postValue(Pair(UiStates(NetworkReqStates.SUCCESS, null),ReverseGeocodingLocation(weatherForecastObj.name, null, null, weatherForecastObj.sys.country, null)))
        } else if (weatherForecastRes.errorBody() != null) {
            _weatherForecastState.emit(
                Pair(
                    UiStates(
                        NetworkReqStates.ERROR,
                        weatherForecastRes.message()
                    ), null
                )
            )
        }
    }

    suspend fun saveSearchQuery(searchQuery: String) {
        if(searchQueriesDAO.doSearchQueryExist(searchQuery)==0)
            searchQueriesDAO.insertOne(SearchQueries(searchQuery))
    }
    private fun cancelAllCoroutines(message: String){
        viewModelScope.cancel(message)
    }

    override fun onCleared() {
        super.onCleared()
        locationManager.currentLocation.removeObserver(currentLocationObserver)
        reverseGeocodingState.removeObserver(geocodingLocationObserver)
    }
}