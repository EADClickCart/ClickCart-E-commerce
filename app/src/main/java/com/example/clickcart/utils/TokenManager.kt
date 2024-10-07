// File: app/src/main/java/com/example/clickcart/utils/TokenManager.kt

package com.example.clickcart.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import android.util.Log
import org.json.JSONObject
import java.nio.charset.StandardCharsets

object TokenManager {
    private const val PREF_NAME = "TokenPrefs"
    private const val KEY_TOKEN = "token"
    private const val KEY_IS_ACTIVE = "is_active"
    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply()
    }

    fun saveIsActive(isActive: Boolean) {
        sharedPreferences.edit().putBoolean(KEY_IS_ACTIVE, isActive).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun getIsActive(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_ACTIVE, false)
    }

    fun getUserRole(): String {
        val token = getToken()
        return try {
            val payload = decodeTokenPayload(token)
            payload.getString("role")
        } catch (e: Exception) {
            Log.e("TokenManager", "Error getting user role", e)
            ""
        }
    }

    fun getUserId(): String? {
        val token = getToken()
        return try {
            val payload = decodeTokenPayload(token)
            payload.getString("nameid")
        } catch (e: Exception) {
            Log.e("TokenManager", "Error getting user ID", e)
            null
        }
    }

    private fun decodeTokenPayload(token: String?): JSONObject {
        if (token == null) throw IllegalArgumentException("Token is null")

        val parts = token.split(".")
        if (parts.size != 3) throw IllegalArgumentException("Invalid token format")

        val payload = parts[1]
        val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
        val decodedString = String(decodedBytes, StandardCharsets.UTF_8)

        return JSONObject(decodedString)
    }

    fun clearToken() {
        sharedPreferences.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_IS_ACTIVE)
            .apply()
    }

    fun getUserEmail(): String {
        val token = getToken()
        return try {
            val payload = decodeTokenPayload(token)
            payload.getString("email")
        } catch (e: Exception) {
            Log.e("TokenManager", "Error getting user email", e)
            ""
        }
    }
}