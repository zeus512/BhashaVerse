package com.jiostar.bhashaverse.domain


import retrofit2.Response

interface UseCase<in Params, out Type> {
    suspend operator fun invoke(params: Params): Result<Type>
}

sealed class AppError(message: String? = null) : Throwable(message) {
    data class NetworkError(val errorMessage: String) : AppError(errorMessage)
    data class EmptyBodyError(val errorMessage: String = "Response body is empty") :
        AppError(errorMessage)

    data class UnknownError(val errorMessage: String? = null) : AppError(errorMessage)
}

fun <T> Response<T>.toResult(): Result<T> {
    return if (this.isSuccessful) {
        val body = this.body()
        if (body != null) {
            Result.success(body)
        } else {
            Result.failure(AppError.EmptyBodyError())
        }
    } else {
        Result.failure(AppError.NetworkError("Request failed with code: ${this.code()} and message: ${this.message()}"))
    }
}

suspend fun <T> Result<T>.execute(
    onSuccess: suspend (T) -> Unit, onError: suspend (AppError) -> Unit
) {
    this.onSuccess {
        onSuccess(it)
    }.onFailure {
        when (it) {
            is AppError -> onError(it)
            else -> onError(AppError.UnknownError(it.message))
        }
    }
}