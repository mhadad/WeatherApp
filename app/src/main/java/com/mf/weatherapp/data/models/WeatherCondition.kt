package com.mf.weatherapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherCondition(@SerialName("id") var id: Int, @SerialName("main") var main: String, @SerialName("description") var description: String, @SerialName("icon") var icon: String) {
}