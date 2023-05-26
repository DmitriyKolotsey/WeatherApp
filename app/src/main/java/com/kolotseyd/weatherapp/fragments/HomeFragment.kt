package com.kolotseyd.weatherapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kolotseyd.weatherapp.R


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        val searchFragment = SearchFragment()
        val settingsFragment = SettingsFragment()
        val currentWeatherFragment = CurrentWeatherFragment()

        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.flFragmentContainer, currentWeatherFragment).commit()

        val bottomNavigationView: BottomNavigationView = view.findViewById(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    requireActivity().supportFragmentManager.beginTransaction().replace(R.id.flFragmentContainer, currentWeatherFragment).commit()
                    true
                }
                R.id.search -> {
                    requireActivity().supportFragmentManager.beginTransaction().replace(R.id.flFragmentContainer, searchFragment).commit()
                    true
                }
                R.id.settings -> {
                    requireActivity().supportFragmentManager.beginTransaction().replace(R.id.flFragmentContainer, settingsFragment).commit()
                    true
                }
                else -> false
            }
        }

        return view
    }



}