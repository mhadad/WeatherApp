package com.mf.weatherapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationCoordinates(@SerialName("lon") val lon : String, @SerialName("lat") val lat: String) {

}