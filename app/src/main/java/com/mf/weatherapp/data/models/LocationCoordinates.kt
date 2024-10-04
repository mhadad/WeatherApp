package com.mf.weatherapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocationCoordinates(@SerialName("lon") var lon : String, @SerialName("lat") var lat: String) {

}