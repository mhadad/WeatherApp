package com.mf.weatherapp.ui.views.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mf.weatherapp.R
import com.mf.weatherapp.data.models.NetworkReqStates
import com.mf.weatherapp.data.models.ReverseGeocodingLocation
import com.mf.weatherapp.data.models.WeatherForecast
import com.mf.weatherapp.view_models.WeatherForecastViewModel
import java.util.Date

@Composable()
fun WeatherConditions(weatherForecastViewModel: WeatherForecastViewModel = hiltViewModel<WeatherForecastViewModel>()) {
//    val weatherForecastCurrentState = weatherForecastViewModel.weatherForecastState.collectAsStateWithLifecycle()
    val weatherForecastCurrentState = weatherForecastViewModel.weatherForecastState.observeAsState()
    var reverseGeocodingState = weatherForecastViewModel.reverseGeocodingState.observeAsState()
    var isLoading by remember {
        mutableStateOf(weatherForecastCurrentState.value?.first?.networkReqStates)
    }
    LaunchedEffect( weatherForecastCurrentState) {
        isLoading = weatherForecastCurrentState.value?.first?.networkReqStates
    }
    when(weatherForecastCurrentState.value?.first?.networkReqStates){
                NetworkReqStates.EMPTY -> showEmptyScreen()
                NetworkReqStates.REQUESTED -> CircularProgressIndicator(modifier = Modifier.fillMaxSize(1.0f))
                NetworkReqStates.LOADING -> CircularProgressIndicator(modifier = Modifier.fillMaxSize(1.0f))
                NetworkReqStates.ERROR -> showError(weatherForecastCurrentState.value?.first?.errorMessage)
                NetworkReqStates.SUCCESS -> showWeatherConditions(weatherForecast = weatherForecastCurrentState.value?.second)
                else -> Unit
            }
    when(reverseGeocodingState.value?.first?.networkReqStates){
        NetworkReqStates.REQUESTED -> CircularProgressIndicator(modifier = Modifier.fillMaxSize(1.0f))
        NetworkReqStates.LOADING -> CircularProgressIndicator(modifier = Modifier.fillMaxSize(1.0f))
        NetworkReqStates.ERROR -> showError(message = reverseGeocodingState.value?.first?.errorMessage)
        NetworkReqStates.SUCCESS -> showLocationDetails(locationData = reverseGeocodingState.value?.second)
        else -> Unit
    }
}

@Composable()
fun showEmptyScreen(){
    Column(modifier = Modifier.fillMaxSize(0.7f)) {
        Text("Please look for a location weather using the search bar or click on the location button to acquire lcoation")
    }
}
@Composable()
fun showError(message: String?){
    Log.e("Data", message ?: "NULL but error has been returned")
    Column(modifier = Modifier.fillMaxSize(0.7f)) {
        Text("An error has occurred: ${message}")
    }
}

@Composable()
fun showWeatherConditions(weatherForecast: WeatherForecast?){
    val columnScrollState = rememberScrollState()
    val icon = weatherForecast?.weather?.get(0)?.icon ?: "01d"
    var image = R.drawable.ic_sunny
    when (icon){
     "01d", "02d" -> image = R.drawable.ic_sunny
     "10d" -> image = R.drawable.ic_rainy_sunny
     "02n", "03d", "04d", "04n", "03n" -> image = R.drawable.ic_cloudy
     "09d", "10n", "09n" -> image = R.drawable.ic_rainy
     "11d" -> image = R.drawable.ic_thunder_lightning
     "13d", "13n" -> image = R.drawable.ic_snow
     "50d", "50n" ->   image = R.drawable.ic_fog
     "01n" ->  image = R.drawable.ic_clear
     else -> image = R.drawable.ic_cloudy
    }

    Box(modifier = Modifier.fillMaxSize(0.7f).fillMaxWidth(1.0f)) {
        Column(
            modifier = Modifier
                .padding(25.dp)
                .verticalScroll(columnScrollState).align(alignment = Alignment.Center)
        ) {
            Text(
                text = Date().toString(),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                color = Color.Blue,
                fontSize = 9.sp,

                )

            Image(
                painter = painterResource(id = image),
                contentDescription = "${
                    weatherForecast?.weather?.get(0)?.description ?: weatherForecast?.weather?.get(0)?.main
                }"
            ) // Indicate the weather condition using contentDesc
            Text("Temp. now: ${weatherForecast?.main?.temp}")
            Text("Max Temp. today: ${weatherForecast?.main?.temp_max}")
            Text("Min Temp.: ${weatherForecast?.main?.temp_min}")
            Text("Humidity : ${weatherForecast?.main?.humidity}")
            Text("Pressure : ${weatherForecast?.main?.pressure}")
            Text("Temp. feels like : ${weatherForecast?.main?.feels_like}")
        }
    }
            }

@Composable()
fun showLocationDetails(locationData: ReverseGeocodingLocation?){
    Text("Country/City/State: ${locationData?.country}/${locationData?.name}/${locationData?.state ?: ""}")
}



