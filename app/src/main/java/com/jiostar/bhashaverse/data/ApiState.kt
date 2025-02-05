package com.jiostar.bhashaverse.data

sealed class ApiState<out T> {
    data object Loading : ApiState<Nothing>()
    data class Success<out T>(val data: T) : ApiState<T>()
    data class Failure(val message: String) : ApiState<Nothing>()
}