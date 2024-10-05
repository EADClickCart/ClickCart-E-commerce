package com.example.clickcart.api

import com.example.clickcart.data.UserResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path



interface UserApiService {
    @GET("api/User/{userId}")
    fun getUserDetails(@Path("userId") userId: String): Call<UserResponse>
}
