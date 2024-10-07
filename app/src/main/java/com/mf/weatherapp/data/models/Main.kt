package com.mf.weatherapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Main(@SerialName("temp") val temp: Float,@SerialName("feels_like") val feels_like: Float,@SerialName("temp_max") val temp_max : Float,@SerialName("temp_min") val temp_min: Float,@SerialName("pressure") val pressure : Int,@SerialName("humidity") val humidity: Int,@SerialName("sea_level") val sea_level: Int,@SerialName("grnd_level") val grnd_level : Int) {
}