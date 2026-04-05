# 06 Database

## Tooling
- We will use **Room Database**, the Android Jetpack component providing an abstraction layer over SQLite.

## Database Class
```kotlin
@Database(entities = [FavoriteDog::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDogDao(): FavoriteDogDao
}
```

## Data Access Object (DAO)
```kotlin
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
```

## Migration Strategy
- Since version is 1, no migrations are needed yet. Use `fallbackToDestructiveMigration()` during early development to avoid version bumps when schemas change.
