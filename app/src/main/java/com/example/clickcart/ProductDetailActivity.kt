package com.example.clickcart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import com.example.clickcart.models.Product

class ProductDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

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

        // Get the product from the intent
        val product = intent.getSerializableExtra("PRODUCT") as Product

        // Bind views
        val productNameDetail: TextView = findViewById(R.id.productNameDetail)
        val productDescriptionDetail: TextView = findViewById(R.id.productDescriptionDetail)
        val productPriceDetail: TextView = findViewById(R.id.productPriceDetail)
        val productImageView: ImageView = findViewById(R.id.imageView)

        // Set the product details in the views
        productNameDetail.text = product.name
        productDescriptionDetail.text = product.description
        productPriceDetail.text = "${product.price}$"

        // Set a common image for now
        productImageView.setImageResource(R.drawable.product_img)
    }
}
