package com.jiostar.bhashaverse.domain.usecase

import com.jiostar.bhashaverse.data.ApiService
import com.jiostar.bhashaverse.data.models.User
import com.jiostar.bhashaverse.domain.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetUsersUseCase @Inject constructor(
    private val apiService: ApiService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher = Dispatchers.IO // Inject a dispatcher for testing
) {
    suspend operator fun invoke(
        onSuccess: (List<User>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        withContext(dispatcher) {
            try {
                val response = apiService.getUsers()
                if (response.isSuccessful) {
                    response.body()?.let { users ->
                        onSuccess(users) // Call onSuccess with the data
                    } ?: onFailure("Empty response body") // Call onFailure for empty body
                } else {
                    onFailure("Request failed with code: ${response.code()}") // Call onFailure for unsuccessful response
                }
            } catch (e: HttpException) {
                onFailure("An unexpected error occurred") // Call onFailure for HTTP exceptions
            } catch (e: IOException) {
                onFailure("Couldn't reach server. Check your internet connection.") // Call onFailure for IO exceptions
            }
        }
    }
}