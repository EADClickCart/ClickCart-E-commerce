package com.example.clickcart.models

import java.util.Date

data class Order(
    val _id: String? = null,
    val customerId: String,
    val customerName: String,
    val orderItems: List<OrderItem>,
    val status: OrderStatus,
    val orderDate: Date = Date(),
    val totalPrice: Double
)
