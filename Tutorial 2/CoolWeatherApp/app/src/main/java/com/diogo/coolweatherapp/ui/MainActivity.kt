package com.diogo.coolweatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.diogo.coolweatherapp.R
import com.diogo.coolweatherapp.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: WeatherViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Default: Lisbon coordinates
    private var currentLat = 38.76
    private var currentLon = -9.12

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineGranted || coarseGranted) {
            fetchGpsAndLoad()
        } else {
            // Fall back to default Lisbon coordinates
            viewModel.fetchWeather(currentLat, currentLon)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply Day/Night theme programmatically (Listing 1 & 2 from tutorial)
        applyDayNightTheme()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Observe LiveData from ViewModel (MVVM Observer pattern)
        viewModel.weatherState.observe(this) { state ->
            handleWeatherState(state)
        }

        // Update button: parse EditText and reload
        binding.btnUpdate.setOnClickListener {
            val input = binding.etCoordinates.text.toString().trim()
            parseCoordinates(input)?.let { (lat, lon) ->
                currentLat = lat
                currentLon = lon
                viewModel.fetchWeather(lat, lon)
            } ?: Toast.makeText(this, "Invalid format. Use: lat,lon (e.g. 38.76,-9.12)", Toast.LENGTH_SHORT).show()
        }

        // On startup: request GPS permission and fetch real location
        requestLocationAndFetch()
    }

    /** Applies Theme.Day or Theme.Night based on current hour */
    private fun applyDayNightTheme() {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val isDay = hour in 6..19
        setTheme(if (isDay) R.style.Theme_Day else R.style.Theme_Night)
    }

    private fun requestLocationAndFetch() {
        val fineGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarseGranted = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted || coarseGranted) {
            fetchGpsAndLoad()
        } else {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchGpsAndLoad() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLat = location.latitude
                currentLon = location.longitude
            }
            viewModel.fetchWeather(currentLat, currentLon)
        }.addOnFailureListener {
            viewModel.fetchWeather(currentLat, currentLon)
        }
    }

    private fun handleWeatherState(state: WeatherViewModel.WeatherState) {
        when (state) {
            is WeatherViewModel.WeatherState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.tvError.visibility = View.GONE
            }
            is WeatherViewModel.WeatherState.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.tvError.visibility = View.GONE
                updateUI(state)
            }
            is WeatherViewModel.WeatherState.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = "Error: ${state.message}"
            }
        }
    }

    private fun updateUI(state: WeatherViewModel.WeatherState.Success) {
        val data = state.data
        val cw = data.currentWeather

        // Coordinates display
        binding.etCoordinates.setText("%.4f,%.4f".format(state.lat, state.lon))

        // Current weather values
        binding.tvTemperature.text = getString(R.string.temperature_format, cw.temperature)
        binding.tvWindspeed.text = getString(R.string.windspeed_format, cw.windspeed)
        binding.tvTime.text = getString(R.string.time_format, cw.time)

        // Find current hour index for pressure
        val now = cw.time.take(13) // "YYYY-MM-DDTHH"
        val idx = data.hourly.time.indexOfFirst { it.startsWith(now) }.takeIf { it >= 0 } ?: 0
        val pressure = data.hourly.pressureMsl.getOrNull(idx)
        binding.tvPressure.text = getString(R.string.pressure_format, pressure ?: 0.0)

        // WMO weather code → label + icon (from XML string-array)
        val (label, iconRes) = getWeatherCodeInfo(cw.weathercode)
        binding.tvWeatherDesc.text = label
        binding.ivWeatherIcon.setImageResource(iconRes)
    }

    /**
     * Maps WMO weather codes to descriptions and drawable icons.
     * Uses string-array XML resource (Section 2.2 XML Resources challenge).
     */
    private fun getWeatherCodeInfo(code: Int): Pair<String, Int> {
        val codes    = resources.getIntArray(R.array.wmo_codes)
        val descs    = resources.getStringArray(R.array.wmo_descriptions)
        val iconKeys = resources.getStringArray(R.array.wmo_icon_names)

        val idx = codes.indexOfFirst { it == code }.takeIf { it >= 0 } ?: 0
        val description = descs.getOrElse(idx) { "Unknown" }
        val iconName    = iconKeys.getOrElse(idx) { "ic_weather_unknown" }
        val iconRes  = resources.getIdentifier(iconName, "drawable", packageName)
        return Pair(description, if (iconRes != 0) iconRes else R.drawable.ic_weather_unknown)
    }

    private fun parseCoordinates(input: String): Pair<Double, Double>? {
        return try {
            val parts = input.split(",")
            if (parts.size != 2) return null
            val lat = parts[0].trim().toDouble()
            val lon = parts[1].trim().toDouble()
            if (lat in -90.0..90.0 && lon in -180.0..180.0) Pair(lat, lon) else null
        } catch (e: NumberFormatException) {
            null
        }
    }
}
