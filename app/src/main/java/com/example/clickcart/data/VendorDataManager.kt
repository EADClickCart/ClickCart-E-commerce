package com.example.clickcart.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.clickcart.api.VendorApiService
import com.example.clickcart.api.RetrofitClient
import com.example.clickcart.data.UserResponse
import com.example.clickcart.models.VendorRatingResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object VendorDataManager {
    private const val TAG = "VendorDataManager"
    private val api = RetrofitClient.create().create(VendorApiService::class.java)

    private val vendorCache = mutableMapOf<String, UserResponse>()
    private val vendorRatingCache = mutableMapOf<String, Double>()

    private val _vendorDetails = MutableLiveData<Map<String, UserResponse>>()
    val vendorDetails: LiveData<Map<String, UserResponse>> = _vendorDetails

    private val _vendorRatings = MutableLiveData<Map<String, Double>>()
    val vendorRatings: LiveData<Map<String, Double>> = _vendorRatings

    suspend fun getVendorDetails(vendorId: String): UserResponse {
        return suspendCoroutine { continuation ->
            vendorCache[vendorId]?.let {
                Log.d(TAG, "Returning cached vendor details for $vendorId")
                continuation.resume(it)
                return@suspendCoroutine
            }

            Log.d(TAG, "Fetching vendor details for $vendorId")
            api.getUserDetails(vendorId).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val vendor = response.body()!!
                        vendorCache[vendorId] = vendor
                        _vendorDetails.postValue(vendorCache.toMap())
                        Log.d(TAG, "Successfully fetched vendor details for $vendorId")
                        continuation.resume(vendor)
                    } else {
                        val error = "Failed to fetch vendor details: ${response.code()}"
                        Log.e(TAG, error)
                        continuation.resumeWithException(Exception(error))
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.e(TAG, "Network error while fetching vendor details", t)
                    continuation.resumeWithException(t)
                }
            })
        }
    }

    suspend fun getVendorRating(vendorId: String): Double {
        return suspendCoroutine { continuation ->
            vendorRatingCache[vendorId]?.let {
                Log.d(TAG, "Returning cached vendor rating for $vendorId")
                continuation.resume(it)
                return@suspendCoroutine
            }

            Log.d(TAG, "Fetching vendor rating for $vendorId")
            api.getVendorRating(vendorId).enqueue(object : Callback<VendorRatingResponse> {
                override fun onResponse(call: Call<VendorRatingResponse>, response: Response<VendorRatingResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val rating = response.body()!!.averageRating
                        vendorRatingCache[vendorId] = rating
                        _vendorRatings.postValue(vendorRatingCache.toMap())
                        Log.d(TAG, "Successfully fetched vendor rating for $vendorId: $rating")
                        continuation.resume(rating)
                    } else {
                        val error = "Failed to fetch vendor rating: ${response.code()}"
                        Log.e(TAG, error)
                        continuation.resumeWithException(Exception(error))
                    }
                }

                override fun onFailure(call: Call<VendorRatingResponse>, t: Throwable) {
                    Log.e(TAG, "Network error while fetching vendor rating", t)
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}