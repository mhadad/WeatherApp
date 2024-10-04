package com.mf.weatherapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReverseGeocodingLocation(@SerialName("name") val name : String, @SerialName("lat") val lat : Double?, @SerialName("lon") val lon: Double?, @SerialName("country") val country: String, @SerialName("state") val state: String?) {

}