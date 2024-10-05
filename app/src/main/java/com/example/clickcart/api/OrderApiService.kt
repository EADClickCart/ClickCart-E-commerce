package com.example.clickcart.api

import com.example.clickcart.models.Order
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body

interface OrderApiService {
    @GET("api/Order/{userID}")
    fun getUserOrders(@Path("userID") userID: String): Call<List<Order>>

    @POST("api/Order/create")
    fun createOrder(@Body order: Order): Call<Order>
}