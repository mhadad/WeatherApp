package com.mf.weatherapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Wind(@SerialName("speed") var speed: Float,@SerialName("deg") var deg: Int,@SerialName("gust") var gust : Float) {
}