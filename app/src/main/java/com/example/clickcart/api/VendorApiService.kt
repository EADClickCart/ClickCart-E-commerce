package com.example.clickcart.api

import com.example.clickcart.data.UserResponse
import com.example.clickcart.models.VendorRatingResponse
import com.example.clickcart.models.VendorReview
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Path

interface VendorApiService {

    @GET("api/User/{userId}")
    fun getUserDetails(@Path("userId") userId: String): Call<UserResponse>

    @GET("api/vendor-review/{vendorId}/average-rating")
    fun getVendorRating(@Path("vendorId") vendorId: String): Call<VendorRatingResponse>

    @GET("api/vendor-review/{vendorId}")
    suspend fun getVendorReviews(
        @Path("vendorId") vendorId: String
    ): Response<List<VendorReview>>
}