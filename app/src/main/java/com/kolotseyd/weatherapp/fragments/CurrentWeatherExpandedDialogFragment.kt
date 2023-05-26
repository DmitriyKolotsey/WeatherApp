package com.kolotseyd.weatherapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.kolotseyd.weatherapp.R
import com.kolotseyd.weatherapp.data.WeatherData
import com.kolotseyd.weatherapp.data.WeatherViewModel
import com.kolotseyd.weatherapp.view.GradientTextViewGray

class CurrentWeatherExpandedDialogFragment : DialogFragment() {

    private lateinit var weatherViewModel: WeatherViewModel
    lateinit var weatherData: WeatherData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.dialog_fragment_current_weather_expanded, container, false)

        weatherViewModel = ViewModelProvider(requireActivity()).get(WeatherViewModel::class.java)
        weatherViewModel.getCurrentWeatherData().observe(requireActivity()) {
            weatherData = it
        }

        val tvClose: TextView = view.findViewById(R.id.tvClose)
        tvClose.setOnClickListener(View.OnClickListener {
            dismiss()
        })

        val tvWind: GradientTextViewGray = view.findViewById(R.id.tvWind)
        tvWind.text = "Wind speed: \n${weatherData.current.wind_kph} kph"
        val tvWindDegree: GradientTextViewGray = view.findViewById(R.id.tvWindDegree)
        tvWindDegree.text = "Wind degree: \n${weatherData.current.wind_degree}°"
        val tvWindDirection: GradientTextViewGray = view.findViewById(R.id.tvWindDirection)
        tvWindDirection.text = "Wind direction: \n${weatherData.current.wind_dir}"
        val tvWindGust: GradientTextViewGray = view.findViewById(R.id.tvWindGust)
        tvWindGust.text = "Wind gust: \n${weatherData.current.gust_kph} kph"

        val tvPressure: GradientTextViewGray = view.findViewById(R.id.tvPressure)
        tvPressure.text = "Pressure: \n${weatherData.current.pressure_mb} mb"
        val tvHumidity: GradientTextViewGray = view.findViewById(R.id.tvHumidity)
        tvHumidity.text = "Humidity: \n${weatherData.current.humidity}%"

        val tvClouds: GradientTextViewGray = view.findViewById(R.id.tvClouds)
        tvClouds.text = "Clouds: \n${weatherData.current.cloud}%"
        val tvVisibility: GradientTextViewGray = view.findViewById(R.id.tvVisibility)
        tvVisibility.text = "Visibility: \n${weatherData.current.vis_km} km"

        val tvUVIndex: GradientTextViewGray = view.findViewById(R.id.tvUVIndex)
        tvUVIndex.text = "UV index: ${weatherData.current.uv}"

        return view
    }

}