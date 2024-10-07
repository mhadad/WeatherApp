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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mf.weatherapp.R
import com.mf.weatherapp.data.models.NetworkReqStates
import com.mf.weatherapp.data.models.ReverseGeocodingLocation
import com.mf.weatherapp.data.models.UiStates
import com.mf.weatherapp.data.models.WeatherForecast
import com.mf.weatherapp.view_models.WeatherForecastViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Date

@Composable()
fun WeatherConditions(weatherForecastViewModel: WeatherForecastViewModel = hiltViewModel<WeatherForecastViewModel>()) {

    val weatherForecastCurrentState = weatherForecastViewModel.weatherForecastState.collectAsStateWithLifecycle()
    val sharedFlow = weatherForecastViewModel.weatherForecastStateSharedFlow.collectAsStateWithLifecycle(initialValue = Pair(UiStates(NetworkReqStates.LOADING, null), null))
    var reverseGeocodingState = weatherForecastViewModel.reverseGeocodingState.observeAsState()
    var isLoading by remember {
        mutableStateOf(false)
    }
    LaunchedEffect( sharedFlow, reverseGeocodingState) {
        weatherForecastCurrentState.value?.let {
            isLoading = (it.first.networkReqStates == NetworkReqStates.LOADING)
        }
    }
    weatherForecastCurrentState.value?.let {
        when{
            isLoading -> CircularProgressIndicator(modifier = Modifier.fillMaxSize(1.0f))
            it.first.networkReqStates == NetworkReqStates.EMPTY -> showEmptyScreen()
            it.first.networkReqStates == NetworkReqStates.REQUESTED -> CircularProgressIndicator(modifier = Modifier.fillMaxSize(1.0f))
            it.first.networkReqStates == NetworkReqStates.ERROR -> showError(it.first.errorMessage)
            it.first.networkReqStates == NetworkReqStates.SUCCESS -> showWeatherConditions(weatherForecast = it.second)
            else -> Unit
        }
    }
    reverseGeocodingState.value?.let{
        if(it.first.networkReqStates == NetworkReqStates.SUCCESS)
            showLocationDetails(locationData = it.second)
    }


}

@Composable()
fun showEmptyScreen(){
    Column(modifier = Modifier.fillMaxSize(0.7f)) {
        Text("Please look for a location weather using the search bar or click on the location button to acquire lcoation. Enter your USA Zip Code, or City name and countryCode comma separated or City name,State Code, Country Code.")
    }
}
@Composable()
fun showError(message: String?){
    message?.let{
        Log.e("Data", message )
        Column(modifier = Modifier.fillMaxSize(0.7f)) {
            Text("An error has occurred: ${message}")
        }
    }

}

@Composable()
fun showWeatherConditions(weatherForecast: WeatherForecast?){
    weatherForecast?.let {
        val columnScrollState = rememberScrollState()
        val icon = it.weather.get(0).icon ?: "01d"
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

        Box(modifier = Modifier
            .fillMaxSize(0.7f)
            .fillMaxWidth(1.0f)) {
            Column(
                modifier = Modifier
                    .padding(25.dp)
                    .verticalScroll(columnScrollState)
                    .align(Alignment.Center)
            ) {
                Text(
                    text = Date().toString(),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    color = Color.Blue,
                    fontSize = 15.sp,

                    )
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(image)
                        .build(),
                    contentDescription = "${
                        it.weather.get(0).description 
                    }"
                )
                Text("Temp. now: ${it.main.temp}", fontSize = 13.sp,)
                Text("Max Temp. today: ${it.main.temp_max}", fontSize = 13.sp,)
                Text("Min Temp.: ${it.main.temp_min}", fontSize = 13.sp,)
                Text("Humidity : ${it.main.humidity}", fontSize = 13.sp,)
                Text("Pressure : ${it.main.pressure}", fontSize = 13.sp,)
                Text("Temp. feels like : ${it.main.feels_like}", fontSize = 13.sp,)
            }
        }

    }
}

@Composable()
fun showLocationDetails(locationData: ReverseGeocodingLocation?){
    Text("Country/City/State: ${locationData?.country}/${locationData?.name}}")
}



