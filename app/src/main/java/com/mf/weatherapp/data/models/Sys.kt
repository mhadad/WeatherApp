package com.mf.weatherapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sys(@SerialName("type") var type: Int, @SerialName("id") var id : Int, @SerialName("country") var country : String, @SerialName("sunrise") var sunrise : Long, @SerialName("sunset") var sunset: Long) {
}