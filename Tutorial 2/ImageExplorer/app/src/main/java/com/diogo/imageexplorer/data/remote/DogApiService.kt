package com.diogo.imageexplorer.data.remote

import com.diogo.imageexplorer.data.model.RandomDogResponse
import retrofit2.http.GET

interface DogApiService {
    @GET("breeds/image/random/50")
    suspend fun getRandomDogs(): RandomDogResponse
}
