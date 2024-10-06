package com.example.clickcart

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.clickcart.fragment.Account
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.clickcart.fragment.Home
import com.example.clickcart.fragment.Order
import com.example.clickcart.fragment.Cart
import com.example.clickcart.utils.TokenManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize TokenManager
        TokenManager.init(this)

        // Check if user is logged in
        val token = TokenManager.getToken()

        if (token == null) {
            // Redirect to LoginActivity if token is missing
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Continue loading the HomeFragment
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, Home())
                    .commit()
            }
            if (intent.getBooleanExtra("SHOW_HOME", false)) {
                showHomeFragment()
            }

            // Set up bottom navigation
            val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
            bottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        replaceFragment(Home())
                        true
                    }
                    R.id.wishlist -> {
                        replaceFragment(Cart())
                        true
                    }
                    R.id.order -> {
                        replaceFragment(Order())
                        true
                    }
                    R.id.account -> {
                        replaceFragment(Account())
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun showHomeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, Home())
            .commit()
    }
}

