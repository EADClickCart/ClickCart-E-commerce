package com.example.clickcart.api

import com.example.clickcart.models.Product
import retrofit2.Call
import retrofit2.http.GET

interface ProductApiService {
    @GET("api/Product")
    fun getProducts(): Call<List<Product>>
}
