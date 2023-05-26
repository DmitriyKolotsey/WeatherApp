package com.kolotseyd.weatherapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.kolotseyd.weatherapp.R
import com.kolotseyd.weatherapp.data.WeatherData
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class SearchResultDialogFragment : DialogFragment() {

    private lateinit var tvCurrentTemperature: TextView
    private lateinit var tvCityName: TextView
    private lateinit var tvCurrentWeatherDescription: TextView
    private lateinit var ivWeatherIcon: ImageView
    private lateinit var tvPrecipitation: TextView
    private lateinit var tvFeelsLike: TextView
    private lateinit var tvWind: TextView
    private lateinit var tvCurrentDate: TextView

    companion object {
        fun newInstance(weatherData: WeatherData): SearchResultDialogFragment {
            val fragment = SearchResultDialogFragment()
            val args = Bundle()
            args.putSerializable("weatherData", weatherData)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.dialog_fragment_search_result, container, false)
        val weatherData: WeatherData = arguments?.getSerializable("weatherData") as WeatherData

        val calendar = Calendar.getInstance()
        val dayOfWeekDateFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
        val monthDateFormat = SimpleDateFormat("MMMM", Locale.ENGLISH)
        val dayOfWeekName = dayOfWeekDateFormat.format(calendar.time)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_MONTH)
        val monthName = monthDateFormat.format(calendar.time)

        tvCurrentDate = view.findViewById(R.id.tvCurrentDate)
        tvCurrentDate.text = "$dayOfWeekName, $monthName $dayOfWeek"

        tvCurrentTemperature = view.findViewById(R.id.tvCurrentTemperature)
        tvCityName = view.findViewById(R.id.tvCityName)
        tvCurrentWeatherDescription = view.findViewById(R.id.tvCurrentWeatherDescription)
        ivWeatherIcon = view.findViewById(R.id.ivMainWeatherIcon)
        tvPrecipitation = view.findViewById(R.id.tvPrecipitation)
        tvFeelsLike = view.findViewById(R.id.tvFeelsLike)
        tvWind = view.findViewById(R.id.tvWind)

        tvCurrentTemperature.text = weatherData.current.temp_c.toString()
        tvCityName.text = weatherData.location.name
        tvCurrentWeatherDescription.text = weatherData.current.condition.text
        Glide
            .with(requireContext())
            .load("https:${weatherData.current.condition.icon}")
            .fitCenter()
            .into(ivWeatherIcon)
        tvPrecipitation.text= "${weatherData.current.precip_mm} mm"
        tvFeelsLike.text = "${weatherData.current.feelslike_c} °C"
        tvWind.text = "${weatherData.current.wind_kph} kph"

        val ivBack: ImageView = view.findViewById(R.id.ivBack)
        ivBack.setOnClickListener {
            dismiss()
        }

        return view
    }

}