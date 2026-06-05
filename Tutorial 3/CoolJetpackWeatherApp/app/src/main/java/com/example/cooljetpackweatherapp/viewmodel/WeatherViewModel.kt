package com.example.cooljetpackweatherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cooljetpackweatherapp.data.WeatherApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(WeatherUIState())
    val uiState: StateFlow<WeatherUIState> = _uiState.asStateFlow()

    init {
        fetchWeather()
    }

    fun updateLatitude(lat: Float) {
        _uiState.update { it.copy(latitude = lat) }
    }

    fun updateLongitude(lon: Float) {
        _uiState.update { it.copy(longitude = lon) }
    }

    fun fetchWeather() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val currentLat = _uiState.value.latitude
            val currentLon = _uiState.value.longitude

            val response = WeatherApiClient.getWeather(currentLat, currentLon)
            if (response != null && response.currentWeather != null) {
                _uiState.update {
                    it.copy(
                        temperature = response.currentWeather.temperature.toFloat(),
                        windspeed = response.currentWeather.windspeed.toFloat(),
                        winddirection = response.currentWeather.winddirection.toInt(),
                        weathercode = response.currentWeather.weathercode,
                        seaLevelPressure = response.hourly?.pressureMsl?.firstOrNull()?.toFloat() ?: 0f,
                        time = response.currentWeather.time,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = "Failed to fetch weather data") }
            }
        }
    }

    /**
     * Adds the current latitude/longitude as a favorite with the given name.
     */
    fun addFavorite(name: String) {
        val currentState = _uiState.value
        val newFavorite = FavoriteLocation(
            name = name,
            latitude = currentState.latitude,
            longitude = currentState.longitude
        )
        _uiState.update { it.copy(favorites = it.favorites + newFavorite) }
    }

    /**
     * Selects a favorite location: sets its coordinates and automatically fetches weather.
     */
    fun selectFavorite(favorite: FavoriteLocation) {
        _uiState.update {
            it.copy(
                latitude = favorite.latitude,
                longitude = favorite.longitude
            )
        }
        fetchWeather()
    }
}
