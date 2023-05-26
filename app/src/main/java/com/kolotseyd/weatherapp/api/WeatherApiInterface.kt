package com.kolotseyd.weatherapp.api

import com.kolotseyd.weatherapp.data.WeatherData
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiInterface {
    @GET("forecast.json?aqi=no&days=3&alerts=no")
    fun getCurrentWeatherAsync(
        @Query("key") apiKey: String,
        @Query("q") location: String
    ): Deferred<WeatherData>
}