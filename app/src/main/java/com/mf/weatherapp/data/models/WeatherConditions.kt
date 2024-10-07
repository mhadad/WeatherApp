package com.mf.weatherapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherConditions(@SerialName("weather") val weather: List<WeatherCondition>) {
}