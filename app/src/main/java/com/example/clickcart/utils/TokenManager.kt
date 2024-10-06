package com.example.clickcart.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import org.json.JSONObject

object TokenManager {
    private const val PREF_NAME = "userPrefs"
    private const val TOKEN_KEY = "userToken"
    private const val USER_ID_KEY = "userId"
    private const val USER_ROLE_KEY = "userRole"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
        // Decode the token and extract user ID and role
        val tokenInfo = extractInfoFromToken(token)
        tokenInfo.userId?.let { saveUserId(it) }
        tokenInfo.userRole?.let { saveUserRole(it) }
    }

    private fun extractInfoFromToken(token: String): TokenInfo {
        return try {
            val payload = token.split(".")[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
            val decodedPayload = String(decodedBytes)
            val jsonObject = JSONObject(decodedPayload)
            TokenInfo(
                userId = jsonObject.optString("nameid"),
                userRole = jsonObject.optString("role")
            )
        } catch (e: Exception) {
            e.printStackTrace()
            TokenInfo(null, null)
        }
    }

    private fun saveUserId(userId: String) {
        sharedPreferences.edit().putString(USER_ID_KEY, userId).apply()
    }

    private fun saveUserRole(userRole: String) {
        sharedPreferences.edit().putString(USER_ROLE_KEY, userRole).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun getUserId(): String? {
        return sharedPreferences.getString(USER_ID_KEY, null)
    }

    fun getUserRole(): String? {
        return sharedPreferences.getString(USER_ROLE_KEY, null)
    }

    fun clearToken() {
        sharedPreferences.edit().clear().apply()
    }

    data class TokenInfo(val userId: String?, val userRole: String?)
}