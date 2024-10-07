package com.example.clickcart.data

data class LoginResponse(
    val token: String,
    val role: String,
    val isActive: Boolean
)