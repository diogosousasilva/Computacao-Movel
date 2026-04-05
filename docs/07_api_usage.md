# 07 API Usage

## The Dog CEO API
This API is completely free and requires no Authentication or API Keys.

**Base URL**: `https://dog.ceo/api/`

## Endpoints

### 1. Random Images for Home Screen
`GET /breeds/image/random/50`
- **Description**: Fetches 50 random dog images.
- **Use Case**: Populating the initial grid.

### 2. List All Breeds
`GET /breeds/list/all`
- **Description**: Fetches a map of all breeds to sub-breeds.
- **Use Case**: Populating a filter dropdown.

### 3. Images by Breed
`GET /breed/{breed_name}/images`
- **Description**: Fetches all images belonging to a specific breed.
- **Use Case**: Applied when the user clicks a specific breed from the filter.

## Rate Limiting & Error Handling
There is no hard rate limit, but reasonable caching should be implemented.
We should implement Retrofit with a generic network error interceptor or generic `try-catch` to map `IOException` and `HttpException` to a user-friendly `UiState.Error`.
