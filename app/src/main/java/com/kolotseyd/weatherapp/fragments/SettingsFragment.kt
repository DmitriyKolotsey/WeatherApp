package com.kolotseyd.weatherapp.fragments

import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.kolotseyd.weatherapp.R

class SettingsFragment : Fragment() {

    private val APP_SETTINGS_NAME = "setting"
    private val APP_SETTINGS_LOCATION = "location"
    private val APP_SETTINGS_LOCATION_NAME = "location_name"
    private val APP_SETTINGS_NOTIFICATIONS = "notifications"

    private lateinit var settings: SharedPreferences

    private lateinit var cbLocationSetting: CheckBox
    private lateinit var cbNotificationsSetting: CheckBox
    private lateinit var etLocationSetting: EditText



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_settings, container, false)

        cbLocationSetting = view.findViewById(R.id.cbLocationSetting)
        cbNotificationsSetting = view.findViewById(R.id.cbNotificationsSetting)
        etLocationSetting = view.findViewById(R.id.etLocationSetting)

        val llLocationSetting: LinearLayout = view.findViewById(R.id.llLocationSetting)

        settings = requireActivity().getSharedPreferences(APP_SETTINGS_NAME, AppCompatActivity.MODE_PRIVATE)

        cbLocationSetting.isChecked = settings.getBoolean(APP_SETTINGS_LOCATION, true)
        cbNotificationsSetting.isChecked = settings.getBoolean(APP_SETTINGS_NOTIFICATIONS, true)

        cbLocationSetting.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isChecked) {
                llLocationSetting.visibility = View.GONE
            } else {
                llLocationSetting.visibility = View.VISIBLE
            }
        }

        return view
    }

    override fun onStop() {
        super.onStop()
        Log.d("TAG", "onStop")

        val editor: Editor = settings.edit()
        editor.putBoolean(APP_SETTINGS_LOCATION, cbLocationSetting.isChecked)
        editor.putBoolean(APP_SETTINGS_NOTIFICATIONS, cbNotificationsSetting.isChecked)
        if (!cbLocationSetting.isChecked) {
            val locationName: String = etLocationSetting.text.toString()
            editor.putString(APP_SETTINGS_LOCATION_NAME, locationName)
        }
        editor.apply()
    }
}