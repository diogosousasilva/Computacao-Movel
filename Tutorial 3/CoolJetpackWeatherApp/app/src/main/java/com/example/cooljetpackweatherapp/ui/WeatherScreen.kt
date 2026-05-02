package com.example.cooljetpackweatherapp.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cooljetpackweatherapp.viewmodel.WeatherViewModel

@Composable
fun WeatherUI(weatherViewModel: WeatherViewModel = viewModel()) {
    val weatherUIState by weatherViewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        LandscapeWeatherUI(
            latitude = weatherUIState.latitude,
            longitude = weatherUIState.longitude,
            temperature = weatherUIState.temperature,
            windSpeed = weatherUIState.windspeed,
            windDirection = weatherUIState.winddirection,
            seaLevelPressure = weatherUIState.seaLevelPressure,
            time = weatherUIState.time,
            onLatitudeChange = { newValue ->
                newValue.toFloatOrNull()?.let { weatherViewModel.updateLatitude(it) }
            },
            onLongitudeChange = { newValue ->
                newValue.toFloatOrNull()?.let { weatherViewModel.updateLongitude(it) }
            },
            onUpdateButtonClick = { weatherViewModel.fetchWeather() }
        )
    } else {
        PortraitWeatherUI(
            latitude = weatherUIState.latitude,
            longitude = weatherUIState.longitude,
            temperature = weatherUIState.temperature,
            windSpeed = weatherUIState.windspeed,
            windDirection = weatherUIState.winddirection,
            seaLevelPressure = weatherUIState.seaLevelPressure,
            time = weatherUIState.time,
            onLatitudeChange = { newValue ->
                newValue.toFloatOrNull()?.let { weatherViewModel.updateLatitude(it) }
            },
            onLongitudeChange = { newValue ->
                newValue.toFloatOrNull()?.let { weatherViewModel.updateLongitude(it) }
            },
            onUpdateButtonClick = { weatherViewModel.fetchWeather() }
        )
    }
}

@Composable
fun PortraitWeatherUI(
    latitude: Float,
    longitude: Float,
    temperature: Float,
    windSpeed: Float,
    windDirection: Int,
    seaLevelPressure: Float,
    time: String,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onUpdateButtonClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        CoordinatesCard(
            latitude = latitude,
            longitude = longitude,
            onLatitudeChange = onLatitudeChange,
            onLongitudeChange = onLongitudeChange
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onUpdateButtonClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Weather")
        }
        Spacer(modifier = Modifier.height(16.dp))
        WeatherCard(
            temperature = temperature,
            windSpeed = windSpeed,
            windDirection = windDirection,
            seaLevelPressure = seaLevelPressure,
            time = time
        )
    }
}

@Composable
fun LandscapeWeatherUI(
    latitude: Float,
    longitude: Float,
    temperature: Float,
    windSpeed: Float,
    windDirection: Int,
    seaLevelPressure: Float,
    time: String,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onUpdateButtonClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            CoordinatesCard(
                latitude = latitude,
                longitude = longitude,
                onLatitudeChange = onLatitudeChange,
                onLongitudeChange = onLongitudeChange
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onUpdateButtonClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Update Weather")
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            WeatherCard(
                temperature = temperature,
                windSpeed = windSpeed,
                windDirection = windDirection,
                seaLevelPressure = seaLevelPressure,
                time = time
            )
        }
    }
}
