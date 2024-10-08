package com.example.clickcart.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.clickcart.models.VendorReview

class VendorViewModel : ViewModel() {
    private val _reviews = MutableLiveData<List<VendorReview>>()
    val reviews: LiveData<List<VendorReview>> = _reviews

    fun setReviews(reviews: List<VendorReview>) {
        _reviews.value = reviews
    }
}