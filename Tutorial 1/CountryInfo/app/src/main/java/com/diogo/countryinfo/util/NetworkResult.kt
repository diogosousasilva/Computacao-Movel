package com.diogo.countryinfo.util

/**
 * Sealed class representing the state of a network request.
 */
sealed class NetworkResult<out T> {
    data object Idle : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String) : NetworkResult<Nothing>()
}
