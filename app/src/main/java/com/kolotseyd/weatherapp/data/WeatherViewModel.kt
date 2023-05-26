package com.kolotseyd.weatherapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WeatherViewModel : ViewModel() {
    private val currentWeatherLiveData = MutableLiveData<WeatherData>()

    fun setCurrentWeatherData(weatherData: WeatherData) {
        currentWeatherLiveData.value = weatherData
    }

    fun getCurrentWeatherData() : LiveData<WeatherData> {
        return currentWeatherLiveData
    }
}