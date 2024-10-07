package com.example.clickcart.api

import com.example.clickcart.models.Order
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Body

interface OrderApiService {
    @GET("api/Order/customer/{userID}")
    fun getUserOrders(@Path("userID") userID: String): Call<List<Order>>

    @GET("api/Order/{orderID}")
    fun getOrderDetails(@Path("orderID") orderID: String): Call<Order>

    @POST("api/Order/create")
    fun createOrder(@Body order: Order): Call<Order>
}