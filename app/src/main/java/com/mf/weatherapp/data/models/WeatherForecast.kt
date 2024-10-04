package com.mf.weatherapp.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//{
//    "coord": {
//    "lon": 10.99,
//    "lat": 44.34
//},
//    "weather": [
//    {
//        "id": 804,
//        "main": "Clouds",
//        "description": "overcast clouds",
//        "icon": "04n"
//    }
//    ],
//    "base": "stations",
//    "main": {
//    "temp": 285.85,
//    "feels_like": 285.4,
//    "temp_min": 283.85,
//    "temp_max": 285.97,
//    "pressure": 1017,
//    "humidity": 85,
//    "sea_level": 1017,
//    "grnd_level": 950
//},
//    "visibility": 10000,
//    "wind": {
//    "speed": 1.46,
//    "deg": 175,
//    "gust": 1.76
//},
//    "clouds": {
//    "all": 100
//},
//    "dt": 1727754041,
//    "sys": {
//    "type": 2,
//    "id": 2075663,
//    "country": "IT",
//    "sunrise": 1727759637,
//    "sunset": 1727801826
//},
//    "timezone": 7200,
//    "id": 3163858,
//    "name": "Zocca",
//    "cod": 200
//}
@Serializable
data class WeatherForecast( @SerialName("cord") val cord: LocationCoordinates, @SerialName("weather") val weather: Array<WeatherCondition>, @SerialName("base") val base: String, @SerialName("main") val main: Main, @SerialName("visibility") val visibility: Int, @SerialName("wind") val wind: Wind, @SerialName("clouds") val clouds: Clouds, @SerialName("dt") val dt: Long, @SerialName("sys") val sys: Sys, @SerialName("timezone") val timezone: Int, @SerialName("id") val id: Long, @SerialName("name") val name: String, @SerialName("cod") val cod: Int ) {

}