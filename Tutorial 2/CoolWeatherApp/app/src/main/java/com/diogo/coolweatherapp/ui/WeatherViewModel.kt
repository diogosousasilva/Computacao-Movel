package com.diogo.coolweatherapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.diogo.coolweatherapp.data.model.WeatherData
import com.diogo.coolweatherapp.data.repository.WeatherRepository

/**
 * WeatherViewModel — MVVM ViewModel (Section 2.2 MVVM Challenge).
 * Exposes state via LiveData so MainActivity can observe reactively.
 */
class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeatherRepository(application)

    private val _weatherState = MutableLiveData<WeatherState>()
    val weatherState: LiveData<WeatherState> = _weatherState

    sealed class WeatherState {
        data object Loading : WeatherState()
        data class Success(val data: WeatherData, val lat: Double, val lon: Double) : WeatherState()
        data class Error(val message: String) : WeatherState()
    }

    fun fetchWeather(latitude: Double, longitude: Double) {
        _weatherState.postValue(WeatherState.Loading)
        Thread {
            try {
                val data = repository.fetchWeather(latitude, longitude)
                _weatherState.postValue(WeatherState.Success(data, latitude, longitude))
            } catch (e: Exception) {
                _weatherState.postValue(WeatherState.Error(e.message ?: "Unknown error"))
            }
        }.start()
    }
}
