package me.cniekirk.flex.data

import okio.IOException
import retrofit2.HttpException
import java.net.HttpURLConnection

sealed class Cause {
    object NoConnection : Cause()
    object NetworkError : Cause()
    object NotFound : Cause()
    object ServerError : Cause()
    object Unknown : Cause()
    object InsufficientStorage : Cause()
    object Unauthenticated : Cause()
}

fun Throwable.map(): Cause {
    return when (this) {
        is IOException -> Cause.NoConnection
        is HttpException -> {
            when (this.code()) {
                HttpURLConnection.HTTP_NOT_FOUND -> Cause.NotFound
                HttpURLConnection.HTTP_UNAVAILABLE -> Cause.NetworkError
                HttpURLConnection.HTTP_INTERNAL_ERROR -> Cause.ServerError
                else -> Cause.Unknown
            }
        }
        else -> Cause.Unknown
    }
}