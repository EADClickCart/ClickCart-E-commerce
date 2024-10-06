package com.example.clickcart.data

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val role: String = "Customer",
    val isActive: Boolean = false
)