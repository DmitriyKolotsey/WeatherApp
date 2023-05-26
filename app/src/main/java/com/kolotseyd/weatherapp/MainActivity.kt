package com.kolotseyd.weatherapp

import android.Manifest
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.kolotseyd.weatherapp.api.WeatherApiInterface
import com.kolotseyd.weatherapp.data.NotificationJobService
import com.kolotseyd.weatherapp.data.WeatherData
import com.kolotseyd.weatherapp.data.WeatherViewModel
import com.kolotseyd.weatherapp.fragments.HomeFragment
import com.kolotseyd.weatherapp.fragments.SplashScreenFragment
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private val API_KEY: String = "eacf36476caa4207a98114637231304"
    private val baseURL: String = "https://api.weatherapi.com/v1/"

    private val APP_SETTINGS_NAME = "setting"
    private val APP_SETTINGS_LOCATION = "location"
    private val APP_SETTINGS_LOCATION_LONGITUDE = "location_longitude"
    private val APP_SETTINGS_LOCATION_LATITUDE = "location_latitude"
    private val APP_SETTINGS_LOCATION_NAME = "location_name"
    private val APP_SETTINGS_NOTIFICATIONS = "notifications"
    var isFirstRun = true

    var latitude: String = ""
    var longitude: String = ""

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var weatherViewModel: WeatherViewModel

    private lateinit var weatherApiInterface: WeatherApiInterface

    private lateinit var settings: SharedPreferences

    lateinit var weatherData: WeatherData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        settings = getSharedPreferences(APP_SETTINGS_NAME, MODE_PRIVATE)

        if (isFirstRun) {
            isFirstRun = false
            val editor: Editor = settings.edit()
            editor.putBoolean(APP_SETTINGS_LOCATION, true)
            editor.putBoolean(APP_SETTINGS_NOTIFICATIONS, true)
            editor.putString(APP_SETTINGS_LOCATION_NAME, "London")
            editor.apply()
        }

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        weatherApiInterface = retrofit.create(WeatherApiInterface::class.java)

        val splashScreenFragment = SplashScreenFragment()
        supportFragmentManager.beginTransaction().replace(R.id.flMainActivityFragmentContainer, splashScreenFragment).commit()

        if (settings.getBoolean(APP_SETTINGS_LOCATION, true)) {
            getWeatherDataWithLocation()
        } else {
            getWeatherDataWithCityName()
        }

        val JOB_ID = 12345

        val jobScheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        val componentName = ComponentName(this, NotificationJobService::class.java)

        val morningCalendar = Calendar.getInstance()
        morningCalendar.set(Calendar.HOUR_OF_DAY, 17)
        morningCalendar.set(Calendar.MINUTE, 25)
        morningCalendar.set(Calendar.SECOND, 0)
        val morningTriggerTime = morningCalendar.timeInMillis - System.currentTimeMillis()

        val eveningCalendar = Calendar.getInstance()
        eveningCalendar.set(Calendar.HOUR_OF_DAY, 20)
        eveningCalendar.set(Calendar.MINUTE, 0)
        eveningCalendar.set(Calendar.SECOND, 0)
        val eveningTriggerTime = eveningCalendar.timeInMillis - System.currentTimeMillis()

        val jobInfoMorning = JobInfo.Builder(JOB_ID, componentName)
            .setMinimumLatency(morningTriggerTime)
            .setRequiresDeviceIdle(false)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .build()

        val jobInfoEvening = JobInfo.Builder(JOB_ID + 1, componentName)
            .setMinimumLatency(eveningTriggerTime)
            .setRequiresDeviceIdle(false)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
            .build()

        jobScheduler.schedule(jobInfoMorning)
        jobScheduler.schedule(jobInfoEvening)
    }

    private fun checkPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }
    }

    private fun getWeatherDataWithLocation() {
        checkPermission()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    location?.let {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()

                        val editor: Editor = settings.edit()
                        editor.putString(APP_SETTINGS_LOCATION_LATITUDE, latitude)
                        editor.putString(APP_SETTINGS_LOCATION_LONGITUDE, longitude)
                        editor.apply()

                        locationManager.removeUpdates(locationListener)


                        GlobalScope.launch(Dispatchers.IO) {
                            try {
                                val currentWeatherDataDeferred = weatherApiInterface.getCurrentWeatherAsync(API_KEY, "$latitude $longitude")
                                weatherData = currentWeatherDataDeferred.await()

                                withContext(Dispatchers.Main) {
                                    weatherViewModel = ViewModelProvider(this@MainActivity).get(WeatherViewModel::class.java)
                                    weatherViewModel.setCurrentWeatherData(weatherData)
                                    val homeFragment = HomeFragment()
                                    supportFragmentManager.beginTransaction().replace(R.id.flMainActivityFragmentContainer, homeFragment).commit()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(applicationContext, "Something went wrong. Try again later", Toast.LENGTH_SHORT).show()
                                finish()
                            }

                        }

                    }
                }
            }

            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0,
                0f,
                locationListener
            )
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Can't detect your location. Check your internet connection or try again later", Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    private fun getWeatherDataWithCityName() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val currentWeatherDataDeferred = weatherApiInterface.getCurrentWeatherAsync(API_KEY,
                    settings.getString(APP_SETTINGS_LOCATION_NAME, "")!!
                )
                weatherData = currentWeatherDataDeferred.await()

                withContext(Dispatchers.Main) {
                    weatherViewModel = ViewModelProvider(this@MainActivity).get(WeatherViewModel::class.java)
                    weatherViewModel.setCurrentWeatherData(weatherData)
                    val homeFragment = HomeFragment()
                    supportFragmentManager.beginTransaction().replace(R.id.flMainActivityFragmentContainer, homeFragment).commit()
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Something went wrong. Try again later", Toast.LENGTH_SHORT).show()
                finish()
            }

        }
    }
}