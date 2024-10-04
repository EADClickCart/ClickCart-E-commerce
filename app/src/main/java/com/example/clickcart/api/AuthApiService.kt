package com.example.clickcart.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String)

interface AuthApiService {
    @POST("/api/User/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}
