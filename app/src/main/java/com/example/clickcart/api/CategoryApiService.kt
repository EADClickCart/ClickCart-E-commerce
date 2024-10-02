package com.example.clickcart.api

import com.example.clickcart.models.Category
import retrofit2.http.GET
import retrofit2.Call

interface CategoryApiService {
    @GET("api/Category")
    fun getCategories(): Call<List<Category>>
}