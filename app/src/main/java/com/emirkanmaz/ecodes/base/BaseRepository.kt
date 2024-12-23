package com.emirkanmaz.ecodes.base

abstract class BaseRepository {

//    suspend fun <T> safeApiCall( apiCall: suspend () -> Response<T> ): Result<T> {
//        return try {
//            val response = apiCall()
//            if (response.isSuccessful) {
//                response.body()?.let { body ->
//                    Result.Success(body)
//                } ?: Result.Error("Response body is null")
//            } else {
//                Result.Error("Error: ${response.code()} - ${response.message()}")
//            }
//
//        } catch (e: Exception) {
//            Result.Error("Error: ${e.message}", e)
//        }
//    }

}