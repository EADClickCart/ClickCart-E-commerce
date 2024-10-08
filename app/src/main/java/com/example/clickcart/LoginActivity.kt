package com.example.clickcart

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clickcart.api.AuthApiService
import com.example.clickcart.data.LoginRequest
import com.example.clickcart.data.LoginResponse
import com.example.clickcart.api.RetrofitClient
import com.example.clickcart.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize TokenManager
        TokenManager.init(this)

        usernameEditText = findViewById(R.id.email_input)
        passwordEditText = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.sign_in_button)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            performLogin(username, password)
        }

        val signUpTextView: TextView? = findViewById(R.id.Sign_up)
        signUpTextView?.setOnClickListener {
            try {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("LoginActivity", "Error starting RegisterActivity", e)
            }
        }
    }

    private fun performLogin(username: String, password: String) {
        val authService = RetrofitClient.create().create(AuthApiService::class.java)
        val loginRequest = LoginRequest(username, password)

        authService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        Log.d("Token", "Token received: ${loginResponse.token}")
                        TokenManager.saveToken(loginResponse.token)
                        TokenManager.saveIsActive(loginResponse.isActive)  // Save isActive status

                        val userRole = TokenManager.getUserRole()
                        val userId = TokenManager.getUserId()
                        Log.d("User Role", "User Role: $userRole")
                        Log.d("User ID", "User ID: $userId")

                        when {
                            userRole != "Customer" -> {
                                Toast.makeText(this@LoginActivity, "Not a valid user", Toast.LENGTH_SHORT).show()
                            }
                            !loginResponse.isActive -> {
                                // Redirect to AccountStatusActivity if account is not active
                                val intent = Intent(this@LoginActivity, AccountStatusActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else -> {
                                // Account is active and role is Customer, proceed to MainActivity
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}