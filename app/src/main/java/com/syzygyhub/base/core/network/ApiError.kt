package com.syzygyhub.base.core.network

import java.io.IOException

sealed class ApiError {
    data class Network(val cause: Throwable) : ApiError()
    data class Http(val code: Int, val message: String) : ApiError()
    data class Unknown(val cause: Throwable) : ApiError()

    companion object {
        fun from(throwable: Throwable): ApiError = when (throwable) {
            is IOException -> Network(throwable)
            else -> Unknown(throwable)
        }
    }
}
