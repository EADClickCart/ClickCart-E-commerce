package com.example.clickcart.models

data class VendorReview(
    val id: String,
    val vendorId: String,
    val customerId: String,
    val comment: String,
    val rating: Int,
    val createdAt: String
)