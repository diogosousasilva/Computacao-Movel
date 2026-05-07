package com.example.cooljetpackweatherapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.cooljetpackweatherapp.R

@Composable
fun WeatherCard(
    temperature: Float,
    windSpeed: Float,
    windDirection: Int,
    seaLevelPressure: Float,
    time: String
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            WeatherRow(label = stringResource(R.string.sea_level_pressure), value = "$seaLevelPressure hPa")
            WeatherRow(label = stringResource(R.string.wind_direction), value = "$windDirection°")
            WeatherRow(label = stringResource(R.string.wind_speed), value = "$windSpeed km/h")
            WeatherRow(label = stringResource(R.string.temperature), value = "$temperature°C")
            WeatherRow(label = stringResource(R.string.time), value = time)
        }
    }
}

@Composable
fun WeatherRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}
