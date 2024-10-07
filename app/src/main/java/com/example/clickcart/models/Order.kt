package com.example.clickcart.models

import java.util.Date

data class Order(
    val id: String? = null,  // Optional if not provided
    val customerId: String,
    val items: List<OrderItem>,
    val status: String,
    val createdAt: String? = null,  // Optional if not provided
    val note: String? = null,
    val isPartiallyDelivered: Boolean = false,
    val vendorStatuses: List<String> = emptyList(),
    val totalOrderPrice: Double,
    val vendorId: String? = null
)

