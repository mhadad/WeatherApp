package com.mf.weatherapp

import androidx.lifecycle.Observer
import com.mf.weatherapp.data.models.UiStates
import com.mf.weatherapp.view_models.WeatherForecastViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.mf.weatherapp.data.models.WeatherForecast
import com.mf.weatherapp.data.repositories.LocationManager
import com.mf.weatherapp.data.repositories.network.ApiService_Kotlin
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class WeatherForecastViewModelTest {

//    @get:Rule
//    val coroutineTestRule = MainDispatcherRule()
//
//
//    @get:Rule
//    val instantExecutorRule = InstantTaskExecutorRule()


    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    // Mock of ApiService
    private lateinit var apiService: ApiService_Kotlin

    // ViewModel instance
    private lateinit var viewModel: WeatherForecastViewModel

    @Mock
    private lateinit var weatherForecastViewModelMockObserver: Observer<Pair<UiStates, WeatherForecast?>>
//        mock(Observer::class.java) as Observer<Pair<UiStates, WeatherForecast?>>

//    private lateinit var weatherForecastViewModel: WeatherForecastViewModel

//    @Mock private var apiService: ApiService = mock(ApiService::class.java)
    private lateinit var weatherObserver: Observer<WeatherForecast>
//    private var locationManager: LocationManager = mock(LocationManager::class.java)
    private var locationManager: LocationManager = mock()

//    private var locationManagerCurrentLocation: MutableLiveData<Pair<NetworkReqStates, Location?>> =
//        mock(MutableLiveData<Pair<NetworkReqStates, Location?>>::class.java)

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
//        weatherForecastViewModelMockObserver =
//        mock(Observer::class.java) as Observer<Pair<UiStates, WeatherForecast?>>

//        viewModel = WeatherForecastViewModel(apiService, locationManager)
//        println(viewModel == null)
//        viewModel.weatherForecastState.observeForever(weatherForecastViewModelMockObserver)
//        println(locationManager == null)
        //println(apiService == null)
//        weatherForecastViewModel = WeatherForecastViewModel(apiService, locationManager)
//        weatherForecastViewModel.weatherForecastState.observeForever(weatherForecastViewModelMockObserver)

//        `when`(weatherForecastViewModel.getLocationDataByZipCode(zipCode = zipCodeQuery)).thenReturn(fakeUser)

    }

    @Test
    fun testDeserializaion(){
    //    fun testUserFetch() = runTest {
//        weatherForecastViewModel.
//        withContext(Dispatchers.Unconfined) {
//            launch {
//                val zipCodeQuery = "92692"
//                var appID = "da33f5713dbaf998ae0fd094c401a24d"
//                val getLocationDataByZipCodeRes = apiService.getLocationDataByZipCode(zipCodeQuery, appID)
//                assert(getLocationDataByZipCodeRes.body()?.name!!.contains("Mission Viejo"))
//            }
//
//        }
        val gson = Gson()
        val jsonStr =
            """        {
    "coord": {
    "lon": 10.99,
    "lat": 44.34
},
    "weather": [
    {
        "id": 804,
        "main": "Clouds",
        "description": "overcast clouds",
        "icon": "04n"
    }
    ],
    "base": "stations",
    "main": {
    "temp": 285.85,
    "feels_like": 285.4,
    "temp_min": 283.85,
    "temp_max": 285.97,
    "pressure": 1017,
    "humidity": 85,
    "sea_level": 1017,
    "grnd_level": 950
},
    "visibility": 10000,
    "wind": {
    "speed": 1.46,
    "deg": 175,
    "gust": 1.76
},
    "clouds": {
    "all": 100
},
    "dt": 1727754041,
    "sys": {
    "type": 2,
    "id": 2075663,
    "country": "IT",
    "sunrise": 1727759637,
    "sunset": 1727801826
},
    "timezone": 7200,
    "id": 3163858,
    "name": "Zocca",
    "cod": 200
}"""
        val deserializedWeatherForecast : WeatherForecast = gson.fromJson(jsonStr, WeatherForecast::class.java)
        assert(deserializedWeatherForecast.main.temp == 285.85f)
    }
}