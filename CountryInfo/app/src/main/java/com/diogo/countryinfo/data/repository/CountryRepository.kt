package com.diogo.countryinfo.data.repository

import com.diogo.countryinfo.data.api.CountryApiService
import com.diogo.countryinfo.data.model.CountryResponse
import com.diogo.countryinfo.util.NetworkResult
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Repository that coordinates API calls to the REST Countries API.
 */
class CountryRepository {

    private val apiService: CountryApiService

    init {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()

        apiService = Retrofit.Builder()
            .baseUrl(CountryApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CountryApiService::class.java)
    }

    /**
     * Search for a country by name.
     * Returns the first matching result.
     */
    suspend fun searchCountry(name: String): NetworkResult<CountryResponse> {
        return try {
            val response = apiService.getCountryByName(name.trim())
            if (response.isNotEmpty()) {
                NetworkResult.Success(response.first())
            } else {
                NetworkResult.Error("Country not found")
            }
        } catch (e: HttpException) {
            if (e.code() == 404) {
                NetworkResult.Error("Country not found. Please check the name and try again.")
            } else {
                NetworkResult.Error("Server error (${e.code()}). Please try again.")
            }
        } catch (e: IOException) {
            NetworkResult.Error("Network error. Please check your connection and try again.")
        } catch (e: Exception) {
            NetworkResult.Error(e.localizedMessage ?: "Something went wrong. Please try again.")
        }
    }
}
