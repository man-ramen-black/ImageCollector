package com.black.imagesearcher.model.data

import okhttp3.Headers

class NetworkResult<T> private constructor(
    val statusCode: Int,
    val headers: Headers?,
    val response: T?,
    val exception: Throwable?) {

    val isSuccess = response != null

    companion object {
        fun <T> success(response: T, statusCode: Int, headers: Headers): NetworkResult<T> {
            return NetworkResult(statusCode, headers, response, null)
        }

        fun <T> success(result: NetworkResult<*>, response: T): NetworkResult<T> {
            return NetworkResult(result.statusCode, result.headers, response, null)
        }

        fun <T> failure(exception: Throwable, statusCode: Int? = null, headers: Headers? = null): NetworkResult<T> {
            return NetworkResult(statusCode ?: -1, headers, null, exception)
        }

        fun <T> failure(result: NetworkResult<*>, exception: Throwable? = result.exception): NetworkResult<T> {
            return NetworkResult(result.statusCode, result.headers, null, exception)
        }
    }

    override fun toString(): String {
        return "NetworkResult(statusCode=$statusCode, headers=$headers, response=$response, exception=$exception)"
    }
}

class ResponseParsingException(message: String = "") : Exception(message)
class UnknownNetworkException(message: String = "") : Exception(message)
class ServerException(message: String = ""): Exception(message)