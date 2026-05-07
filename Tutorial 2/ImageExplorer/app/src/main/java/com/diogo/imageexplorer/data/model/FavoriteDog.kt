package com.diogo.imageexplorer.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_dogs")
data class FavoriteDog(
    @PrimaryKey val imageUrl: String,
    val timestampAdded: Long = System.currentTimeMillis()
)
