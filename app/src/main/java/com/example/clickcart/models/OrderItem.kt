package com.example.clickcart.models

data class OrderItem(
    val _id: String? = null,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val price: Double,
    val totalPrice: Double,
    val status: OrderItemStatus
)