package com.diogo.countryinfo.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response model for the REST Countries API.
 * Endpoint: GET https://restcountries.com/v3.1/name/{name}
 *
 * The API returns a JSON array of country objects.
 */
data class CountryResponse(
    val name: Name,
    val capital: List<String>?,
    val population: Long,
    val region: String,
    val flags: Flags
)

data class Name(
    val common: String,
    val official: String
)

data class Flags(
    val png: String,
    val svg: String?,
    val alt: String?
)
