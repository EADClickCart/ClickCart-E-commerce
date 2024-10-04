package com.example.clickcart.api

import com.example.clickcart.models.Order
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface OrderApiService {
    @GET("orders/{userID}")
    fun getUserOrders(@Path("userID") userID: String): Call<List<Order>>
}
