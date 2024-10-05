package com.example.clickcart.api

import com.example.clickcart.data.LoginRequest
import com.example.clickcart.data.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST




interface AuthApiService {
    @POST("/api/User/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}
