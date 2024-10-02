package com.example.clickcart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Find the TextView with the ID "sign_in"
        val signInTextView: TextView = findViewById(R.id.sign_in)

        // Set a click listener on the TextView
        signInTextView.setOnClickListener {
            // Create an Intent to navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Find the back button
        val backButton: ImageView = findViewById(R.id.back_button)

        // Set a click listener on the ImageView
        backButton.setOnClickListener {
            // Create an Intent to navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        //find the sign up button
        val signUpButton: Button = findViewById(R.id.sign_up_button)

        // Set a click listener on the TextView
        signUpButton.setOnClickListener {
            // Create an Intent to navigate to LoginActivity
            val intent = Intent(this, AccountStatusActivity::class.java)
            startActivity(intent)
        }
    }
}