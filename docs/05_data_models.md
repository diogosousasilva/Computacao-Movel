# 05 Data Models

## API Response Models

### Random Images Response
```kotlin
data class RandomDogResponse(
    @SerializedName("message") val images: List<String>,
    val status: String
)
```

### Breed List Response
```kotlin
data class BreedListResponse(
    @SerializedName("message") val breeds: Map<String, List<String>>,
    val status: String
)
```

## Local Entity Models

### Favorite Dog Entity
```kotlin
@Entity(tableName = "favorite_dogs")
data class FavoriteDog(
    @PrimaryKey val imageUrl: String,
    val breedId: String,
    val timestampAdded: Long = System.currentTimeMillis()
)
```

## Domain Models
```kotlin
data class DogImage(
    val url: String,
    val isFavorite: Boolean = false,
    val inferredBreed: String = ""
)
```
