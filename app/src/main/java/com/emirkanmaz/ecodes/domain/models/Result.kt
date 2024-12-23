package com.emirkanmaz.ecodes.domain.models

sealed class Result<out T> {
    data class Success<out T>(val response: T?) : Result<T>()
    object Loading : Result<Nothing>()
    data class Error(val message: String? = null, val error: Throwable? = null) : Result<Nothing>()
}