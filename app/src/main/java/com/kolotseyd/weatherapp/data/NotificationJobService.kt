package com.kolotseyd.weatherapp.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.kolotseyd.weatherapp.api.WeatherApiInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.util.Calendar

class NotificationJobService : JobService() {

    private val API_KEY: String = "eacf36476caa4207a98114637231304"
    private val baseURL: String = "https://api.weatherapi.com/v1/"

    private val APP_SETTINGS_NAME = "setting"
    private val APP_SETTINGS_LOCATION = "location"
    private val APP_SETTINGS_LOCATION_NAME = "location_name"
    private val APP_SETTINGS_NOTIFICATIONS = "notifications"
    private val APP_SETTINGS_LOCATION_LONGITUDE = "location_longitude"
    private val APP_SETTINGS_LOCATION_LATITUDE = "location_latitude"

    private val CHANNEL_ID = "weather_channel"

    var latitude: String = ""
    var longitude: String = ""

    lateinit var weatherData: WeatherData

    private lateinit var settings: SharedPreferences

    private lateinit var weatherApiInterface: WeatherApiInterface

    override fun onStartJob(p0: JobParameters?): Boolean {
        Log.d("TAG", "Job started")

        settings = applicationContext.getSharedPreferences(APP_SETTINGS_NAME, MODE_PRIVATE)
        latitude = settings.getString(APP_SETTINGS_LOCATION_LATITUDE, "").toString()
        longitude = settings.getString(APP_SETTINGS_LOCATION_LONGITUDE, "").toString()


        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        weatherApiInterface = retrofit.create(WeatherApiInterface::class.java)

        if (settings.getBoolean(APP_SETTINGS_NOTIFICATIONS, true)) {
            if (settings.getBoolean(APP_SETTINGS_LOCATION, true)) {
                getWeatherDataWithLocation()
            } else {
                getWeatherDataWithCityName()
            }
        }

        return true
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        jobFinished(p0, true)
        return true
    }

    private fun getWeatherDataWithLocation() {
        Log.d("TAG", "getWeatherWithLocation")
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val currentWeatherDataDeferred = weatherApiInterface.getCurrentWeatherAsync(API_KEY, "$latitude $longitude")
                weatherData = currentWeatherDataDeferred.await()

                withContext(Dispatchers.Main) {
                    hourCheckAndShowNotification()

                }
            } catch (e: Exception) {
                Log.d("TAG", "Error")
            }
        }
        Log.d("TAG", "${weatherData.current.condition.text}")
    }

    private fun getWeatherDataWithCityName() {
        Log.d("TAG", "getWeatherWithCityName")
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val currentWeatherDataDeferred = weatherApiInterface.getCurrentWeatherAsync(API_KEY,
                    settings.getString(APP_SETTINGS_LOCATION_NAME, "")!!
                )
                weatherData = currentWeatherDataDeferred.await()

                withContext(Dispatchers.Main) {
                    hourCheckAndShowNotification()

                }
            } catch (e: Exception) {
                Log.d("TAG", "Error")
            }
        }
    }

    private fun hourCheckAndShowNotification(){
        val calendar = Calendar.getInstance()
        val calendarHour = calendar.get(Calendar.HOUR_OF_DAY)

        if (calendarHour == 8) {
            showNotification(0)
        } else if (calendarHour == 20) {
            showNotification(1)
        }


    }

    private fun showNotification(day: Int) {
        Glide.with(applicationContext)
            .asBitmap()
            .load("https:${weatherData.forecast.forecastday.get(day).day.condition.icon}")
            .into(object : CustomTarget<Bitmap> () {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    createNotificationChannel()

                    val weatherMaxTemp = weatherData.forecast.forecastday.get(day).day.maxtemp_c
                    val weatherMinTemp = weatherData.forecast.forecastday.get(day).day.mintemp_c
                    val weatherDesc = weatherData.forecast.forecastday.get(day).day.condition.text

                    val notificationText = "$weatherMaxTemp|$weatherMinTemp  $weatherDesc"

                    val notificationTitle: String = if (day == 0) {
                        "Weather today"
                    } else {
                        "Weather tomorrow"
                    }

                    val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                        .setLargeIcon(resource)
                        .setContentTitle(notificationTitle)
                        .setContentText(notificationText)

                    val notification = notificationBuilder.build()

                    notificationManager.notify(1, notification)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }

            })
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Weather Notification Channel"
            val description = "Weather Notification Channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = description

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}