package com.diogo.coolweatherapp.data.repository

import android.content.Context
import com.diogo.coolweatherapp.data.model.WeatherData
import com.google.gson.Gson
import java.net.URL

class WeatherRepository(private val context: Context) {

    private val gson = Gson()

    fun fetchWeather(latitude: Double, longitude: Double): WeatherData {
        val url = buildUrl(latitude, longitude)
        val json = URL(url).readText()
        return gson.fromJson(json, WeatherData::class.java)
    }

    private fun buildUrl(lat: Double, lon: Double): String =
        "https://api.open-meteo.com/v1/forecast" +
            "?latitude=$lat&longitude=$lon" +
            "&current_weather=true" +
            "&hourly=temperature_2m,weathercode,pressure_msl,windspeed_10m"
}
