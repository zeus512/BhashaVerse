package com.jiostar.bhashaverse.data

import com.jiostar.bhashaverse.data.models.User
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("/users")
    suspend fun getUsers(): Response<List<User>>
}

data class Dummy(val dummy: String)