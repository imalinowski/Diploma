package com.malinowski.diploma.model.wifi

sealed class WifiDirectResult<T> {
    class Success<T>(val data: T) : WifiDirectResult<T>()
    class Error<T>(val error: Throwable) : WifiDirectResult<T>()
}