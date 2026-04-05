package com.diogo.coolweatherapp.data.model

import com.google.gson.annotations.SerializedName

data class WeatherData(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    @SerializedName("current_weather") val currentWeather: CurrentWeather,
    val hourly: Hourly
)

data class CurrentWeather(
    val temperature: Double,
    val windspeed: Double,
    val winddirection: Int,
    val weathercode: Int,
    val time: String
)

data class Hourly(
    val time: List<String>,
    @SerializedName("temperature_2m") val temperature2m: List<Double>,
    val weathercode: List<Int>,
    @SerializedName("pressure_msl") val pressureMsl: List<Double>
)