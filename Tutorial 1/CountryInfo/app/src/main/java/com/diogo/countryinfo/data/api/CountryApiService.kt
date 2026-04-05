package com.diogo.countryinfo.data.api

import com.diogo.countryinfo.data.model.CountryResponse
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit interface for the REST Countries API.
 * Base URL: https://restcountries.com/
 */
interface CountryApiService {

    @GET("v3.1/name/{name}")
    suspend fun getCountryByName(@Path("name") name: String): List<CountryResponse>

    companion object {
        const val BASE_URL = "https://restcountries.com/"
    }
}
