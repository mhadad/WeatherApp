package com.mf.weatherapp.ui.views.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mf.weatherapp.ui.views.components.WeatherConditions
import com.mf.weatherapp.ui.views.components.showSearchbar
import dagger.hilt.android.AndroidEntryPoint

@Composable()
fun SearchScreen(innerPadding: PaddingValues, ) {
    Column (modifier = Modifier.padding(innerPadding)){
        showSearchbar()
        WeatherConditions()
    }
}