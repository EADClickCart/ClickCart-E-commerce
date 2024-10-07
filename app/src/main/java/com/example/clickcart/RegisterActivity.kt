// File: app/src/main/java/com/example/clickcart/RegisterActivity.kt

package com.example.clickcart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.clickcart.api.UserApiService
import com.example.clickcart.api.RetrofitClient
import com.example.clickcart.data.RegisterRequest
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var nameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        nameInput = findViewById(R.id.register_screen_name_input)
        emailInput = findViewById(R.id.register_screen_email_input)
        passwordInput = findViewById(R.id.register_screen_password_input)
        signUpButton = findViewById(R.id.sign_up_button)

        val signInTextView: TextView = findViewById(R.id.sign_in)
        val backButton: ImageView = findViewById(R.id.back_button)

        signInTextView.setOnClickListener {
            navigateToLogin()
        }

        backButton.setOnClickListener {
            navigateToLogin()
        }

        signUpButton.setOnClickListener {
            performRegistration()
        }
    }

    private fun performRegistration() {
        val name = nameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val registerRequest = RegisterRequest(
            name = name,
            email = email,
            password = password
        )

        lifecycleScope.launch {
            try {
                val apiService = RetrofitClient.create().create(UserApiService::class.java)
                val response = apiService.registerUser(registerRequest)

                if (response.isSuccessful) {
                    val responseMessage = response.body()?.string() ?: "Registration successful"
                    Toast.makeText(
                        this@RegisterActivity,
                        responseMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToAccountStatus()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Registration failed"
                    Toast.makeText(
                        this@RegisterActivity,
                        errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun navigateToAccountStatus() {
        startActivity(Intent(this, AccountStatusActivity::class.java))
        finish()
    }
}