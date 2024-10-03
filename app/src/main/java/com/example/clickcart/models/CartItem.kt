package com.example.clickcart.models

data class CartItem(
    val productId: String,
    val productName: String,
    val productPrice: Double,
    val quantity: Int = 1
)
