package com.diogo.imageexplorer.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diogo.imageexplorer.data.model.FavoriteDog
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDogDao {
    @Query("SELECT * FROM favorite_dogs ORDER BY timestampAdded DESC")
    fun getAllFavorites(): Flow<List<FavoriteDog>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_dogs WHERE imageUrl = :url)")
    suspend fun isFavorite(url: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(dog: FavoriteDog)

    @Delete
    suspend fun deleteFavorite(dog: FavoriteDog)
}
