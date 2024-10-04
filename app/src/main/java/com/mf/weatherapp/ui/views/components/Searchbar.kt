package com.mf.weatherapp.ui.views.components

import android.app.LocaleConfig
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.mf.weatherapp.R
import com.mf.weatherapp.view_models.WeatherForecastViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

@Preview()
@Composable()
 fun showSearchbar( weatherForecastViewModel: WeatherForecastViewModel = hiltViewModel<WeatherForecastViewModel>()) {
// weatherForecastViewModel.appID = LocalContext.current.getString(R.xml.app_config)
 weatherForecastViewModel.appID = "da33f5713dbaf998ae0fd094c401a24d"
// weatherForecastViewModel.appID = LocalContext.current.resources.getString(R.string.app_id)

 var isExpanded by remember { mutableStateOf(false) }
 var searchZipCode by remember { mutableStateOf("") }
 var searchText by rememberSaveable { mutableStateOf("") }
// val suggestions = mutableListOf("Apple", "Banana", "Cherry", "Date", "Elderberry")
// val filteredSuggestions = suggestions.filter { it.contains(searchText, ignoreCase = true) }.toMutableList()
// var isDDM_Expanded by remember { mutableStateOf(false) }
// val currentLocationState = weatherForecastViewModel.currentLocation.collectAsStateWithLifecycle()
// var currentLocationState = weatherForecastViewModel.currentLocation.observeAsState()
// LaunchedEffect(currentLocationState) {
//  if(currentLocationState.value?.first?.equals(UiStates.SUCCESS) == true)
//  {
//   val location = currentLocationState.value?.second ?: return@LaunchedEffect
//   weatherForecastViewModel.getWeatherForecastByCoordinates(location.latitude.toLong(), location.longitude.toLong())
//  }
////  else if(currentLocationState.value.first.equals(UiStates.ERROR))
// }
// val weatherForecastCurrentState = weatherForecastViewModel.weatherForecastState.collectAsState()

 LaunchedEffect(searchText) {
  if(searchText.isNotEmpty()) {
   Log.e("Data", "Reached")
   launch(Dispatchers.IO) {
    if(searchText.count { it==',' } == 0 && searchText.contains("^[0-9]+$".toRegex()))
     weatherForecastViewModel.getLocationDataByZipCode(searchText)
    else{
     // forward request to the viewModel to detect query type
     weatherForecastViewModel.executeLocationDataQuery(searchText)
    }
   }
  }
 }
 DisposableEffect(LocalLifecycleOwner.current) {
  onDispose {
   weatherForecastViewModel.locationManager.currentLocation.removeObserver(weatherForecastViewModel.currentLocationObserver)
   weatherForecastViewModel.reverseGeocodingState.removeObserver(weatherForecastViewModel.geocodingLocationObserver)
  }
 }
 Column(
  modifier = Modifier
   .fillMaxWidth()
   .padding(16.dp),
  horizontalAlignment = Alignment.CenterHorizontally
 ) {
  // Magnifier Icon to toggle search bar
  Row (modifier = Modifier.align(Alignment.Start)){
   IconButton(onClick = { isExpanded = true }) {
    Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
   }
   IconButton(onClick = {
    // get Location coordinates
//    if(currentLocationState.value?.first?.equals(UiStates.LOADING) == true || currentLocationState.value?.first?.equals(UiStates.REQUESTED)== true){
////     Toast.makeText(, "Trying to acquire GPS location", Toast.LENGTH_SHORT)
//    }
     weatherForecastViewModel.getLocation()
   }) {
    Icon(Icons.Default.LocationOn, contentDescription = "Search Weather using Location")
   }

  // Animated visibility for expanding/collapsing the search bar
  AnimatedVisibility(
   visible = isExpanded,
   enter = expandHorizontally(animationSpec = tween(durationMillis = 300)),
   exit = shrinkHorizontally(animationSpec = tween(durationMillis = 300))
  ) {
   Column(modifier = Modifier.fillMaxWidth()) {
    // Search TextField
    TextField(
     maxLines = 1,
     value = searchZipCode,
     keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
     keyboardActions = KeyboardActions(
      onSearch = {
       searchText = searchZipCode
       isExpanded = false
       println("Search query: $searchZipCode")
      }
     ),
     onValueChange = { newText ->
      searchZipCode = newText
     },
     modifier = Modifier.fillMaxWidth(),/*.onFocusChanged { focusState ->
      isExpanded = focusState.isFocused
      if (!focusState.isFocused) {
       println("TextField lost focus")
      }
     },*/
     placeholder = { Text("Enter your USA Zip Code, or City name and countryCode comma separated or City name,State Code, Country Code") },
    )

   }
  }
 }
  }
}
