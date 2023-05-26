package com.kolotseyd.weatherapp.data

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kolotseyd.weatherapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TemperatureByHourDataAdapter(private val items: List<TemperatureByHourData>) :
    RecyclerView.Adapter<TemperatureByHourDataAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvTemperature: TextView = itemView.findViewById(R.id.tvTemperature)
        val ivWeatherIcon: ImageView = itemView.findViewById(R.id.ivWeatherIcon)
        val tvHour: TextView = itemView.findViewById(R.id.tvHour)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.temperature_rv_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvTemperature.text = "${items[position].temp} °C"

        if (position > 0){
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = Date(items[position].hour.toLong() * 1000)
            val formattedDate = dateFormat.format(date)
            holder.tvHour.text = formattedDate
        } else  holder.tvHour.text = "Now"


        //holder.tvHour.text = items[position].hour
        Glide.with(holder.itemView.context).load("https:${items[position].iconUrl}").into(holder.ivWeatherIcon)
    }
}