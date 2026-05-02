package com.example.cooljetpackweatherapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherData(
    val latitude: Double,
    val longitude: Double,
    @SerialName("current_weather") val currentWeather: CurrentWeather? = null,
    val hourly: Hourly? = null
)

@Serializable
data class CurrentWeather(
    val temperature: Double,
    val windspeed: Double,
    val winddirection: Double,
    val weathercode: Int,
    val time: String
)

@Serializable
data class Hourly(
    val time: List<String>,
    @SerialName("temperature_2m") val temperature2m: List<Double>,
    val weathercode: List<Int>,
    @SerialName("pressure_msl") val pressureMsl: List<Double>,
    @SerialName("windspeed_10m") val windspeed10m: List<Double>
)
