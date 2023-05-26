package com.kolotseyd.weatherapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.kolotseyd.weatherapp.R
import com.kolotseyd.weatherapp.api.WeatherApiInterface
import com.kolotseyd.weatherapp.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class CurrentWeatherFragment : Fragment(), LifecycleOwner {

    private var temperatureByHourArrayList: ArrayList<TemperatureByHourData> = arrayListOf()

    private lateinit var tvCurrentTemperature: TextView
    private lateinit var tvCityName: TextView
    private lateinit var tvCurrentWeatherDescription: TextView
    private lateinit var ivWeatherIcon: ImageView
    private lateinit var tvPrecipitation: TextView
    private lateinit var tvFeelsLike: TextView
    private lateinit var tvWind: TextView
    private lateinit var tvCurrentDate: TextView

    private lateinit var weatherViewModel: WeatherViewModel
    lateinit var weatherData: WeatherData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_current_weather, container, false)

        weatherViewModel = ViewModelProvider(requireActivity()).get(WeatherViewModel::class.java)
        weatherViewModel.getCurrentWeatherData().observe(requireActivity()) {
            weatherData = it
        }

        val calendar = Calendar.getInstance()
        val dayOfWeekDateFormat = SimpleDateFormat("EEEE", Locale.ENGLISH)
        val monthDateFormat = SimpleDateFormat("MMMM", Locale.ENGLISH)
        val dayOfWeekName = dayOfWeekDateFormat.format(calendar.time)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_MONTH)
        val monthName = monthDateFormat.format(calendar.time)

        tvCurrentDate = view.findViewById(R.id.tvCurrentDate)
        tvCurrentDate.text = "$dayOfWeekName, $monthName $dayOfWeek"

        val llReadMore: LinearLayout = view.findViewById(R.id.llReadMore)
        llReadMore.setOnClickListener(View.OnClickListener {
            val currentWeatherExpandedDialogFragment = CurrentWeatherExpandedDialogFragment()
            currentWeatherExpandedDialogFragment.show(childFragmentManager, "CurrentWeatherExpanded")
        })


        temperatureByHourArrayList.add(TemperatureByHourData(weatherData.current.temp_c.toString(), weatherData.current.condition.icon, "Now"))

        for (forecastday in weatherData.forecast.forecastday) {
            for (hour in forecastday.hour){
                if (temperatureByHourArrayList.size <= 23){
                    Log.d("TAG", "time_epoch ${hour.time_epoch.toLong()}   current_time: ${System.currentTimeMillis()/1000}")
                    if (hour.time_epoch.toLong() > System.currentTimeMillis()/1000) {
                        temperatureByHourArrayList.add(TemperatureByHourData(hour.temp_c.toString(), hour.condition.icon, hour.time_epoch.toString()))
                        Log.d("TAG", hour.time_epoch.toString())
                    }
                }
            }
        }

        Log.d("TAG", temperatureByHourArrayList.size.toString())

        val rvTemperatureByHour: RecyclerView = view.findViewById(R.id.rvTemperatureByHour)
        rvTemperatureByHour.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvTemperatureByHour.adapter = TemperatureByHourDataAdapter(temperatureByHourArrayList)

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

        val ivRefresh: ImageView = view.findViewById(R.id.ivRefresh)
        ivRefresh.setOnClickListener {
            getNewDataAndUpdate()
        }

        return view
    }

    private fun getNewDataAndUpdate() {
        val API_KEY = "eacf36476caa4207a98114637231304"
        val baseURL = "https://api.weatherapi.com/v1/"

        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

        val weatherApiInterface = retrofit.create(WeatherApiInterface::class.java)

        GlobalScope.launch(Dispatchers.IO) {
            val currentWeatherDataDeferred = weatherApiInterface.getCurrentWeatherAsync(API_KEY, tvCityName.text.toString())
            weatherData = currentWeatherDataDeferred.await()

            withContext(Dispatchers.Main) {
                weatherViewModel = ViewModelProvider(requireActivity()).get(WeatherViewModel::class.java)
                weatherViewModel.setCurrentWeatherData(weatherData)

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
            }
        }
    }
}
