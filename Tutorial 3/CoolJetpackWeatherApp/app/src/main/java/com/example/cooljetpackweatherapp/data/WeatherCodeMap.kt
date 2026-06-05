package com.example.cooljetpackweatherapp.data

/**
 * Represents a WMO weather code entry with a human-readable description
 * and the corresponding drawable resource name.
 */
data class WeatherCodeInfo(
    val description: String,
    val image: String
)

/**
 * Returns a map of WMO weather codes to their descriptions and drawable image names.
 * These image names correspond to vector drawables in res/drawable/.
 */
fun getWeatherCodeMap(): Map<Int, WeatherCodeInfo> = mapOf(
    0  to WeatherCodeInfo("Clear sky",            "ic_weather_clear"),
    1  to WeatherCodeInfo("Mainly clear",          "ic_weather_mainly_clear"),
    2  to WeatherCodeInfo("Partly cloudy",         "ic_weather_partly_cloudy"),
    3  to WeatherCodeInfo("Overcast",              "ic_weather_overcast"),
    45 to WeatherCodeInfo("Fog",                   "ic_weather_fog"),
    48 to WeatherCodeInfo("Depositing rime fog",   "ic_weather_fog"),
    51 to WeatherCodeInfo("Light drizzle",         "ic_weather_drizzle"),
    53 to WeatherCodeInfo("Moderate drizzle",      "ic_weather_drizzle"),
    55 to WeatherCodeInfo("Dense drizzle",         "ic_weather_drizzle"),
    56 to WeatherCodeInfo("Light freezing drizzle","ic_weather_drizzle"),
    57 to WeatherCodeInfo("Dense freezing drizzle","ic_weather_drizzle"),
    61 to WeatherCodeInfo("Slight rain",           "ic_weather_rain"),
    63 to WeatherCodeInfo("Moderate rain",         "ic_weather_rain"),
    65 to WeatherCodeInfo("Heavy rain",            "ic_weather_heavy_rain"),
    66 to WeatherCodeInfo("Light freezing rain",   "ic_weather_rain"),
    67 to WeatherCodeInfo("Heavy freezing rain",   "ic_weather_heavy_rain"),
    71 to WeatherCodeInfo("Slight snowfall",       "ic_weather_snow"),
    73 to WeatherCodeInfo("Moderate snowfall",     "ic_weather_snow"),
    75 to WeatherCodeInfo("Heavy snowfall",        "ic_weather_snow"),
    77 to WeatherCodeInfo("Snow grains",           "ic_weather_snow"),
    80 to WeatherCodeInfo("Slight rain showers",   "ic_weather_showers"),
    81 to WeatherCodeInfo("Moderate rain showers",  "ic_weather_showers"),
    82 to WeatherCodeInfo("Violent rain showers",   "ic_weather_heavy_rain"),
    85 to WeatherCodeInfo("Slight snow showers",    "ic_weather_snow"),
    86 to WeatherCodeInfo("Heavy snow showers",     "ic_weather_snow"),
    95 to WeatherCodeInfo("Thunderstorm",           "ic_weather_thunderstorm"),
    96 to WeatherCodeInfo("Thunderstorm with slight hail", "ic_weather_thunderstorm"),
    99 to WeatherCodeInfo("Thunderstorm with heavy hail",  "ic_weather_thunderstorm")
)
