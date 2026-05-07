package com.diogo.imageexplorer.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diogo.imageexplorer.data.repository.DogRepository
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val images: List<String>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

class HomeViewModel(private val repository: DogRepository) : ViewModel() {

    private val _uiState = MutableLiveData<HomeUiState>()
    val uiState: LiveData<HomeUiState> = _uiState

    init {
        fetchRandomDogs()
    }

    fun fetchRandomDogs() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val dogs = repository.getRandomDogs()
            if (dogs.isNotEmpty()) {
                _uiState.value = HomeUiState.Success(dogs)
            } else {
                _uiState.value = HomeUiState.Error("Failed to fetch dogs")
            }
        }
    }

    fun toggleFavorite(url: String) {
        viewModelScope.launch {
            repository.toggleFavorite(url)
        }
    }
}
