package com.kolotseyd.weatherapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.kolotseyd.weatherapp.R
import com.kolotseyd.weatherapp.api.WeatherApiInterface
import com.kolotseyd.weatherapp.data.WeatherData
import com.kolotseyd.weatherapp.data.WeatherViewModel
import com.kolotseyd.weatherapp.databinding.FragmentSearchBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SearchFragment : Fragment() {

    private val API_KEY: String = "eacf36476caa4207a98114637231304"
    private val baseURL: String = "https://api.weatherapi.com/v1/"

    private lateinit var weatherViewModel: WeatherViewModel
    lateinit var weatherData: WeatherData

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)

        weatherViewModel = ViewModelProvider(requireActivity()).get(WeatherViewModel::class.java)
        weatherViewModel.getCurrentWeatherData().observe(requireActivity()) {
            weatherData = it
        }

        binding.ivBackButton.setOnClickListener(View.OnClickListener {
            val currentWeatherFragment = CurrentWeatherFragment()
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.flFragmentContainer, currentWeatherFragment).commit()
        })

        binding.tvFirstDayTemperature.text = "${weatherData.forecast.forecastday.get(0).day.maxtemp_c}°"
        binding.tvSecondDayTemperature.text = "${weatherData.forecast.forecastday.get(1).day.maxtemp_c}°"
        binding.tvThirdDayTemperature.text = "${weatherData.forecast.forecastday.get(2).day.maxtemp_c}°"

        Glide
            .with(requireContext())
            .load("https:${weatherData.forecast.forecastday.get(0).day.condition.icon}")
            .fitCenter()
            .into(binding.ivFirstDayWeatherIcon)

        Glide
            .with(requireContext())
            .load("https:${weatherData.forecast.forecastday.get(1).day.condition.icon}")
            .fitCenter()
            .into(binding.ivSecondDayWeatherIcon)

        Glide
            .with(requireContext())
            .load("https:${weatherData.forecast.forecastday.get(2).day.condition.icon}")
            .fitCenter()
            .into(binding.ivThirdDayWeatherIcon)

        binding.tvFirstDayDescription.text = getDaysDescription().get(0)
        binding.tvSecondDayDescription.text = getDaysDescription().get(1)
        binding.tvThirdDayDescription.text = getDaysDescription().get(2)

        binding.ivSearchButton.setOnClickListener(View.OnClickListener {
            getWeatherByEnteredCityName()
        })

        return binding.root
    }

    private fun getDaysDescription(): ArrayList<String> {
        var i = 0
        val daysArrayList: ArrayList<String> = arrayListOf()
        val calendar = Calendar.getInstance()
        val dayOfWeekDateFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
        val monthDateFormat = SimpleDateFormat("MMMM", Locale.ENGLISH)
        val year = calendar.get(Calendar.YEAR)

        while (i <= 3){
            calendar.add(Calendar.DAY_OF_WEEK, i)
            val dayOfWeekName = dayOfWeekDateFormat.format(calendar.time)
            val monthName = monthDateFormat.format(calendar.time)

            daysArrayList.add("$dayOfWeekName \n$monthName $year")

            i++
        }

        return daysArrayList
    }

    private fun getWeatherByEnteredCityName() {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        val weatherApiInterface = retrofit.create(WeatherApiInterface::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val currentWeatherDataDeferred = weatherApiInterface.getCurrentWeatherAsync(API_KEY, "${binding.etSearchLocation.text}")
                weatherData = currentWeatherDataDeferred.await()

                withContext(Dispatchers.Main) {
                    val searchResultDialogFragment = SearchResultDialogFragment.newInstance(weatherData)
                    searchResultDialogFragment.show(requireActivity().supportFragmentManager, "searchResult")
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Something went wrong. Try again later", Toast.LENGTH_SHORT).show()
            }

        }

    }

}