package com.example.clickcart.models

data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val isActive: Boolean,
    val isLowStock: Boolean,
    val lastUpdated: String
)
