package com.example.cooljetpackweatherapp.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.cooljetpackweatherapp.R

@Composable
fun CoordinatesCard(
    latitude: Float,
    longitude: Float,
    onLatitudeChange: (String) -> Unit,
    onLongitudeChange: (String) -> Unit
) {
    val context = LocalContext.current

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = stringResource(R.string.coordinates), style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = {
                    context.startActivity(Intent(context, LocationPickerActivity::class.java))
                }) {
                    Icon(imageVector = Icons.Default.Public, contentDescription = "Pick Location")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = latitude.toString(),
                onValueChange = onLatitudeChange,
                label = { Text(stringResource(R.string.latitude)) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = longitude.toString(),
                onValueChange = onLongitudeChange,
                label = { Text(stringResource(R.string.longitude)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
