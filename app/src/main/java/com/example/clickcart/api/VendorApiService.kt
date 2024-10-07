package com.example.clickcart.api

import com.example.clickcart.data.UserResponse
import com.example.clickcart.models.VendorRatingResponse
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.http.Path

interface VendorApiService {

    @GET("api/User/{userId}")
    fun getUserDetails(@Path("userId") userId: String): Call<UserResponse>

    @GET("api/vendor-review/{vendorId}/average-rating")
    fun getVendorRating(@Path("vendorId") vendorId: String): Call<VendorRatingResponse>
}