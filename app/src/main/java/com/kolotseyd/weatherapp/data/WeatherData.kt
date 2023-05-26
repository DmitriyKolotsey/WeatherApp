package com.kolotseyd.weatherapp.data

import java.io.Serializable

data class WeatherData(var location: Location,
                       var current: Current,
                       var forecast: Forecast) : Serializable

data class Astro(var sunrise: String,
                 var sunset: String)

data class Condition(var text: String,
                     var icon: String,
                     var code: Int)

data class Location(var name: String,
                    var region: String,
                    var country: String,
                    var latitude: Double,
                    var longitude: Double,
                    var tz_id: String,
                    var localtime_epoch: Int,
                    var localtime: String)

data class Current(var last_updated_epoch: Int,
                   var last_updated: String,
                   var temp_c: Double,
                   var condition: Condition,
                   var wind_kph: Double,
                   var wind_degree: Int,
                   var wind_dir: String,
                   var pressure_mb: Double,
                   var precip_mm: Double,
                   var humidity: Int,
                   var cloud: Int,
                   var feelslike_c: Double,
                   var vis_km: Double,
                   var uv: Double,
                   var gust_kph: Double)

data class Day(var maxtemp_c: Double,
                var mintemp_c: Double,
                var avgtemp_c: Double,
                var maxwind_kph: Double,
                var totalprecip_mm: Double,
                var totalsnow_cm: Double,
                var avgsiv_km: Double,
                var avghumidity: Double,
                var condition: Condition)

data class Forecast(var forecastday: ArrayList<Forecastday>)

data class Forecastday(var date: String,
                        var date_epoch: Int,
                        var day: Day,
                        var astro: Astro,
                        var hour: ArrayList<Hour>)

data class Hour(var time_epoch: Int,
                var time: String,
                var temp_c: Double,
                var condition: Condition)
