package com.black.imagesearcher.data.model

sealed class NetworkResult<T> private constructor(open val data: T?, open val response: okhttp3.Response?, open val exception: Throwable?) {
    data class Success<T>(
        override val data: T,
        override val response: okhttp3.Response
    ) : NetworkResult<T>(data, null, null)

    data class Error<T>(
        override val exception: Throwable,
        override val response: okhttp3.Response? = null
    ) : NetworkResult<T>(null, response, exception)

    val isSuccess get() = data != null
}
class ServerError(message: String? = null): Throwable(message)