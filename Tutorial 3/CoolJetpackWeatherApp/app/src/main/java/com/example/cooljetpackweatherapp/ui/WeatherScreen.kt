package com.example.cooljetpackweatherapp.ui

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cooljetpackweatherapp.R
import com.example.cooljetpackweatherapp.viewmodel.WeatherViewModel

@Composable
fun WeatherUI(weatherViewModel: WeatherViewModel = viewModel()) {
    val weatherUIState by weatherViewModel.uiState.collectAsState()
    val configuration = LocalConfiguration.current
    val context = LocalContext.current

    val locationPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { data ->
                val lat = data.getFloatExtra("latitude", weatherUIState.latitude)
                val lon = data.getFloatExtra("longitude", weatherUIState.longitude)
                weatherViewModel.updateLatitude(lat)
                weatherViewModel.updateLongitude(lon)
                weatherViewModel.fetchWeather()
            }
        }
    }

    val onPickLocation: () -> Unit = {
        locationPickerLauncher.launch(Intent(context, LocationPickerActivity::class.java))
    }

    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        LandscapeWeatherUI(
            latitude = weatherUIState.latitude,
            longitude = weatherUIState.longitude,
            temperature = weatherUIState.temperature,
            windSpeed = weatherUIState.windspeed,
            windDirection = weatherUIState.winddirection,
            weathercode = weatherUIState.weathercode,
            seaLevelPressure = weatherUIState.seaLevelPressure,
            time = weatherUIState.time,
            isLoading = weatherUIState.isLoading,
            error = weatherUIState.error,
            onLatitudeChange = { newValue ->
                newValue.toFloatOrNull()?.let { weatherViewModel.updateLatitude(it) }
            },
            onLongitudeChange = { newValue ->
                newValue.toFloatOrNull()?.let { weatherViewModel.updateLongitude(it) }
            },
            onUpdateButtonClick = { weatherViewModel.fetchWeather() },
            onPickLocation = onPickLocation
        )
    } else {
        PortraitWeatherUI(
            latitude = weatherUIState.latitude,
            longitude = weatherUIState.longitude,
            temperature = weatherUIState.temperature,
            windSpeed = weatherUIState.windspeed,
            windDirection = weatherUIState.winddirection,
            weathercode = weatherUIState.weathercode,
            seaLevelPressure = weatherUIState.seaLevelPressure,
            time = weatherUIState.time,
            isLoading = weatherUIState.isLoading,
            error = weatherUIState.error,
            onLatitudeChange = { newValue ->
                newValue.toFloatOrNull()?.let { weatherViewModel.updateLatitude(it) }
            },
            onLongitudeChange = { newValue ->
                newValue.toFloatOrNull()?.let { weatherViewModel.updateLongitude(it) }
            },
            onUpdateButtonClick = { weatherViewModel.fetchWeather() },
            onPickLocation = onPickLocation
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
    weathercode: Int,
    seaLevelPressure: Float,
    time: String,
    isLoading: Boolean,
    error: String?,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onUpdateButtonClick: () -> Unit,
    onPickLocation: () -> Unit,
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
            onLongitudeChange = onLongitudeChange,
            onPickLocation = onPickLocation
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onUpdateButtonClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.update_weather))
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            WeatherCard(
                temperature = temperature,
                windSpeed = windSpeed,
                windDirection = windDirection,
                weathercode = weathercode,
                seaLevelPressure = seaLevelPressure,
                time = time
            )
        }
    }
}

@Composable
fun LandscapeWeatherUI(
    latitude: Float,
    longitude: Float,
    temperature: Float,
    windSpeed: Float,
    windDirection: Int,
    weathercode: Int,
    seaLevelPressure: Float,
    time: String,
    isLoading: Boolean,
    error: String?,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit,
    onUpdateButtonClick: () -> Unit,
    onPickLocation: () -> Unit,
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
                onLongitudeChange = onLongitudeChange,
                onPickLocation = onPickLocation
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onUpdateButtonClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.update_weather))
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                WeatherCard(
                    temperature = temperature,
                    windSpeed = windSpeed,
                    windDirection = windDirection,
                    weathercode = weathercode,
                    seaLevelPressure = seaLevelPressure,
                    time = time
                )
            }
        }
    }
}
