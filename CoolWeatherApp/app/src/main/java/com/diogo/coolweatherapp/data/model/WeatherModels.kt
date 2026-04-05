package com.diogo.coolweatherapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Root Gson mapping for the Open-Meteo /v1/forecast response.
 * Listing 3 from the tutorial PDF.
 */
data class WeatherData(
    @SerializedName("current_weather") val currentWeather: CurrentWeather,
    @SerializedName("hourly")          val hourly: Hourly
)

data class CurrentWeather(
    @SerializedName("temperature")  val temperature: Double,
    @SerializedName("windspeed")    val windspeed: Double,
    @SerializedName("weathercode")  val weathercode: Int,
    @SerializedName("time")         val time: String
)

data class Hourly(
    @SerializedName("time")           val time: ArrayList<String>,
    @SerializedName("temperature_2m") val temperature2m: ArrayList<Double>,
    @SerializedName("weathercode")    val weathercode: ArrayList<Int>,
    @SerializedName("pressure_msl")   val pressureMsl: ArrayList<Double>,
    @SerializedName("windspeed_10m")  val windspeed10m: ArrayList<Double>
)
