package com.diogo.imageexplorer.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diogo.imageexplorer.data.model.FavoriteDog
import com.diogo.imageexplorer.data.repository.DogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: DogRepository) : ViewModel() {

    val favoriteDogs: Flow<List<FavoriteDog>> = repository.getAllFavorites()

    fun toggleFavorite(url: String) {
        viewModelScope.launch {
            repository.toggleFavorite(url)
        }
    }
}
