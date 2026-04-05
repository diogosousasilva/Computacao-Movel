package com.diogo.countryinfo.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diogo.countryinfo.data.model.CountryResponse
import com.diogo.countryinfo.data.repository.CountryRepository
import com.diogo.countryinfo.util.NetworkResult
import kotlinx.coroutines.launch

/**
 * ViewModel for the main screen.
 * Manages country search state.
 */
class CountryViewModel : ViewModel() {

    private val repository = CountryRepository()

    private val _countryResult = MutableLiveData<NetworkResult<CountryResponse>>(NetworkResult.Idle)
    val countryResult: LiveData<NetworkResult<CountryResponse>> = _countryResult

    /**
     * Search for a country by name.
     * Validates input before making the API call.
     */
    fun searchCountry(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) {
            _countryResult.value = NetworkResult.Error("Please enter a country name.")
            return
        }

        _countryResult.value = NetworkResult.Loading
        viewModelScope.launch {
            _countryResult.value = repository.searchCountry(trimmed)
        }
    }
}
