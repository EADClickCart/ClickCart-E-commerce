package com.example.clickcart.models

data class OrderItem(
    val productId: String,
    val quantity: Int,
    val price: Double,
    val totalPrice: Double
)