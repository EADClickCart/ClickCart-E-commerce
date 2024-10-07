package com.example.clickcart.api

import com.example.clickcart.data.RegisterRequest
import com.example.clickcart.data.RegisterResponse
import com.example.clickcart.data.UserResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path



interface UserApiService {
    @GET("api/User/{userId}")
    fun getUserDetails(@Path("userId") userId: String): Call<UserResponse>

    @POST("api/User/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<ResponseBody>

    @PUT("api/User/{id}/update")
    fun updateUserDetails(@Path("id") userId: String, @Body updateBody: Map<String, String>): Call<Unit>
}
