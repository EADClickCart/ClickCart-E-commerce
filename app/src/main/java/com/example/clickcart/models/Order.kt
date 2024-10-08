package com.example.clickcart.models

data class Order(
    val id: String? = null,
    val customerId: String,
    val items: List<OrderItem>,
    val orderStatus: String,
    val createdAt: String? = null,
    val note: String? = null,
    val isPartiallyDelivered: Boolean = false,
    val vendorStatuses: List<String> = emptyList(),
    val totalOrderPrice: Double,
    val vendorId: String? = null
)

