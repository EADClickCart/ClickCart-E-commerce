package com.example.clickcart.api

import com.example.clickcart.utils.TokenManager
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:5296/"

    fun create(): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    class AuthInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val original = chain.request()

            // Retrieve the token from TokenManager
            val token = TokenManager.getToken()

            // If token is not null, add it to the request header
            val requestBuilder = if (token != null) {
                original.newBuilder()
                    .header("Authorization", "Bearer $token")
            } else {
                original.newBuilder()
            }

            val request = requestBuilder.build()
            return chain.proceed(request)
        }
    }
}
