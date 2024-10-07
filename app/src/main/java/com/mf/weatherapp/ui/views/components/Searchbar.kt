package com.mf.weatherapp.ui.views.components

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mf.weatherapp.data.models.SearchQueries
import com.mf.weatherapp.view_models.WeatherForecastViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Preview()
@Composable()
 fun showSearchbar( weatherForecastViewModel: WeatherForecastViewModel = hiltViewModel<WeatherForecastViewModel>()) {
 weatherForecastViewModel.appID = "da33f5713dbaf998ae0fd094c401a24d"
 val context = LocalContext.current
 var hasLocationPermission = ContextCompat.checkSelfPermission(
  context,
  android.Manifest.permission.ACCESS_COARSE_LOCATION
 ) == PackageManager.PERMISSION_GRANTED
 val permissionLauncher = rememberLauncherForActivityResult(
  contract = ActivityResultContracts.RequestPermission(),
 ) { isGranted: Boolean ->
  hasLocationPermission = isGranted
 }

 var isSearchbarExpanded by remember { mutableStateOf(false) }
 var searchQuery by rememberSaveable { mutableStateOf("") }
 var searchText by rememberSaveable { mutableStateOf("") }




 LaunchedEffect(Unit, searchText) {
  if(searchText.isNotEmpty()) {
   launch(Dispatchers.IO) {
    getDataBySearchTxt(searchText, weatherForecastViewModel)
   }
  }
  weatherForecastViewModel.searchQueries.collectLatest{ queries ->
   if(queries.size>0){
     launch(Dispatchers.IO) {
      val searchQueryList = queries.filter { it.searchQuery.isNotEmpty() }
      searchQueryList[0]?.let { // last query from SQLite
       getDataBySearchTxt(it.searchQuery, weatherForecastViewModel)
      }

     }
    }
  }
  if(hasLocationPermission){
    launch(Dispatchers.IO) {
     weatherForecastViewModel.getLocation()
   }

  }
  else if(hasLocationPermission == false)
   permissionLauncher.launch(android.Manifest.permission.ACCESS_COARSE_LOCATION)
 }

 Column(
  modifier = Modifier
   .fillMaxWidth()
   .padding(16.dp),
  horizontalAlignment = Alignment.CenterHorizontally
 ) {
  Row (modifier = Modifier.align(Alignment.Start)){
   IconButton(onClick = { isSearchbarExpanded = !isSearchbarExpanded }) {
    Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
   }
  // Animated visibility for expanding/collapsing the search bar
  AnimatedVisibility(
   visible = isSearchbarExpanded,
   enter = expandHorizontally(animationSpec = tween(durationMillis = 300)),
   exit = shrinkHorizontally(animationSpec = tween(durationMillis = 300))
  ) {
   Column(modifier = Modifier.fillMaxWidth()) {
    // Search TextField
    TextField(
     maxLines = 1,
     value = searchQuery,
     keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
     keyboardActions = KeyboardActions(
      onSearch = {
       searchText = searchQuery
       isSearchbarExpanded = false
       Log.d("Search query:","$searchQuery")
      }
     ),
     onValueChange = { newText ->
      searchQuery = newText
     },
     modifier = Modifier.fillMaxWidth(),
     placeholder = { Text("Search weather data") },
    )
   }
  }
 }
  }

}
suspend fun getDataBySearchTxt(searchQuery: String, weatherForecastViewModel: WeatherForecastViewModel){
 searchQuery.let {
  if(it.trim().isNotEmpty())
   weatherForecastViewModel.saveSearchQuery(searchQuery)
  when {
   it.count { it==',' } == 0 && it.contains("^[0-9]+$".toRegex())-> weatherForecastViewModel.getLocationDataByZipCode(it)
   else -> weatherForecastViewModel.executeLocationDataQuery(it) // forward request to the viewModel to detect query type
   }
  }
}

