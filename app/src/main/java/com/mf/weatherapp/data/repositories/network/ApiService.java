package com.mf.weatherapp.data.repositories.network;

import com.mf.weatherapp.data.models.ReverseGeocodingLocation;
import com.mf.weatherapp.data.models.WeatherForecast;

import java.lang.reflect.Array;
import java.util.ArrayList;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiService {
        @GET("data/2.5/weather")
        Single<WeatherForecast> getWeatherByCoordinates(@Path("lat") long latitude, @Path("lon") long longitude, @Path("units") String units, @Path("appid") String appID);

        @GET("geo/1.0/reverse")
        Single<ArrayList<ReverseGeocodingLocation>> getLocationDataByCoordinates(@Path("lat") long latitude, @Path("lon") long longitude, @Path("limit")Integer limit, @Path("units") String units , @Path("appid") String appID);

        @GET("geo/1.0/zip")
        Single<ReverseGeocodingLocation> getLocationDataByZipCode(@Path("zip") String zipCode_CountryCode,@Path("units") String units ,  @Path("appid") String appID);

        @GET("data/2.5/weather")
        Single<WeatherForecast> getWeatherByCityName(@Path("q") String cityName, @Path("units") String units,  @Path("appid") String appID);

        @GET("data/2.5/weather")
        Single<WeatherForecast> getWeatherByCityName_CountryCode(@Path("q") String cityName_CountryCode,@Path("units") String units, @Path("appid") String appID);

        @GET("data/2.5/weather")
        Single<WeatherForecast>  getWeatherByCityName_StateCode_CountryCode(@Path("q")  String cityName_StateCode_CountryCode, @Path("units") String units , @Path("appid") String appID);
}
