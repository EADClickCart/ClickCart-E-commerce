package com.example.clickcart.models

import java.io.Serializable

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val isActive: Boolean,
    val vendorId: String,
    val isLowStock: Boolean,
    val lastUpdated: String,
    var vendorName: String? = null,
    var rating: Double? = null
): Serializable
