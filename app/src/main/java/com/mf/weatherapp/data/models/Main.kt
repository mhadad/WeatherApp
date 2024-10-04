package com.mf.weatherapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Main(@SerialName("temp") var temp: Float,@SerialName("feels_like") var feels_like: Float,@SerialName("temp_max") var temp_max : Float,@SerialName("temp_min") var temp_min: Float,@SerialName("pressure") var pressure : Int,@SerialName("humidity") var humidity: Int,@SerialName("sea_level") var sea_level: Int,@SerialName("grnd_level") var grnd_level : Int) {
}