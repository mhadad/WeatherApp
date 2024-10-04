package com.mf.weatherapp.data.repositories.network

import com.mf.weatherapp.data.models.ReverseGeocodingLocation
import com.mf.weatherapp.data.models.WeatherForecast
import io.reactivex.rxjava3.core.Observer
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
//    @GET("data/2.5/weather?lat={lat}&lon={long}&appid={app_id}")
    @GET("data/2.5/weather")
    suspend fun getWeatherByCoordinates(@Query("lat") latitude: Long, @Query("lon") longitude: Long, @Query("appid") appID: String): Response<WeatherForecast?>

    @GET("geo/1.0/reverse")
    suspend fun getLocationDataByCoordinates(@Query("lat") latitude: Long, @Query("lon") longitude: Long, @Query("limit") limit: Int, @Query("appid") appID: String): Response<Array<ReverseGeocodingLocation?>>

    @GET("geo/1.0/zip")
    suspend fun getLocationDataByZipCode(@Query("zip") zipCode_CountryCode: String, @Query("appid") appID: String): Response<ReverseGeocodingLocation?>

    @GET("data/2.5/weather")
    suspend fun getWeatherByCityName(@Query("q") cityName: String, @Query("appid") appID: String): Response<WeatherForecast?>

    @GET("data/2.5/weather")
    suspend fun getWeatherByCityName_CountryCode(@Query("q") cityName_CountryCode: String, @Query("appid") appID: String): Response<WeatherForecast?>

    @GET("data/2.5/weather")
    suspend fun  getWeatherByCityName_StateCode_CountryCode(@Query("q") cityName_StateCode_CountryCode: String, @Query("appid") appID: String): Response<WeatherForecast?>
}