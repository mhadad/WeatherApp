package com.mf.weatherapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherCondition(@SerialName("id") val id: Int, @SerialName("main") val main: String, @SerialName("description") val description: String, @SerialName("icon") val icon: String) {
}