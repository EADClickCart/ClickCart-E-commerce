package com.example.clickcart.models

data class OrderItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val price: Double,
    val totalPrice: Double,
    val vendorId: String,
    val vendorName: String,
    val status: String
)