package com.github.example.utils

import com.github.example.entity.Results
import com.github.example.entity.Error
import retrofit2.Response
import java.io.IOException

inline fun <T> processApiResponse(request: () -> Response<T>): Results<T> {
    return try {
        val response = request()
        val responseCode = response.code()
        val responseMessage = response.message()
        if (response.isSuccessful) {
            Results.success(response.body()!!)
        } else {
            Results.failure(Error.NetworkError(responseCode, responseMessage))
        }
    } catch (e: IOException) {
        Results.failure(Error.NetworkError())
    }
}