package com.example.clickcart

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.clickcart.R
import androidx.appcompat.widget.Toolbar

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Initialize the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Remove the app name from the toolbar
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Enable the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle the navigation click
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Use the new way to handle back navigation
        }

        // Handle physical back button with OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // You can customize behavior here if needed
                finish() // Close the activity, behaving like the back button
            }
        })
    }
}
