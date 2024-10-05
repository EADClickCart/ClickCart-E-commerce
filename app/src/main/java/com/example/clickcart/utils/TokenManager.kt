package com.example.clickcart.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import org.json.JSONObject

object TokenManager {
    private const val PREF_NAME = "userPrefs"
    private const val TOKEN_KEY = "userToken"
    private const val USER_ID_KEY = "userId"

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(TOKEN_KEY, token).apply()
        // Decode the token and extract user ID
        val userId = extractUserIdFromToken(token)
        saveUserId(userId)
    }

    private fun extractUserIdFromToken(token: String): String? {
        return try {
            val payload = token.split(".")[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
            val decodedPayload = String(decodedBytes)
            val jsonObject = JSONObject(decodedPayload)
            jsonObject.optString("nameid") // Adjust according to your JWT payload structure
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveUserId(userId: String?) {
        userId?.let {
            sharedPreferences.edit().putString(USER_ID_KEY, it).apply()
        }
    }

    fun getToken(): String? {
        return sharedPreferences.getString(TOKEN_KEY, null)
    }

    fun getUserId(): String? {
        return sharedPreferences.getString(USER_ID_KEY, null)
    }

    fun clearToken() {
        sharedPreferences.edit().clear().apply()
    }
}
