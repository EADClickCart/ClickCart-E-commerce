package com.example.clickcart

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.clickcart.fragment.Home

class AccountStatusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_status)

        // Delay of 2 seconds (2000 milliseconds)
        Handler(Looper.getMainLooper()).postDelayed({
            // Start HomeActivity
            val intent = Intent(this@AccountStatusActivity, Home::class.java)
            startActivity(intent)
            finish() // Optional: Close the current activity so that user cannot come back here
        }, 2000) // Delay in milliseconds (2 seconds)
    }
}
