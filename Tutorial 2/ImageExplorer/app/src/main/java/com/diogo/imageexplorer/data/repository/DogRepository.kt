package com.diogo.imageexplorer.data.repository

import com.diogo.imageexplorer.data.local.FavoriteDogDao
import com.diogo.imageexplorer.data.model.FavoriteDog
import com.diogo.imageexplorer.data.remote.DogApiService
import kotlinx.coroutines.flow.Flow

class DogRepository(
    private val apiService: DogApiService,
    private val dao: FavoriteDogDao
) {
    suspend fun getRandomDogs(): List<String> {
        return try {
            val response = apiService.getRandomDogs()
            if (response.status == "success") response.message else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getAllFavorites(): Flow<List<FavoriteDog>> = dao.getAllFavorites()

    suspend fun isFavorite(url: String): Boolean = dao.isFavorite(url)

    suspend fun toggleFavorite(url: String) {
        if (isFavorite(url)) {
            dao.deleteFavorite(FavoriteDog(url))
        } else {
            dao.insertFavorite(FavoriteDog(url))
        }
    }
}
