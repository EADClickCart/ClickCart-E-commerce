package com.example.clickcart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import com.example.clickcart.models.Product
import com.example.clickcart.models.CartItem
import com.example.clickcart.fragment.Cart

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var product: Product
    private lateinit var addToCartButton: Button

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
            onBackPressedDispatcher.onBackPressed()
        }

        // Handle physical back button with OnBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        // Get the product from the intent
        product = intent.getSerializableExtra("PRODUCT") as Product

        // Bind views
        val productNameDetail: TextView = findViewById(R.id.productNameDetail)
        val productDescriptionDetail: TextView = findViewById(R.id.productDescriptionDetail)
        val productPriceDetail: TextView = findViewById(R.id.productPriceDetail)
        val productImageView: ImageView = findViewById(R.id.imageView)
        addToCartButton = findViewById(R.id.addToCartButton)

        // Set the product details in the views
        productNameDetail.text = product.name
        productDescriptionDetail.text = product.description
        productPriceDetail.text = "$${product.price}"

        // Set a common image for now
        productImageView.setImageResource(R.drawable.product_img)

        // Setup Add to Cart button
        setupAddToCartButton()
    }

    private fun setupAddToCartButton() {
        addToCartButton.setOnClickListener {
            val cartItem = CartItem(
                productId = product.id,
                productName = product.name,
                productPrice = product.price,
                quantity = 1 // You can add quantity selection in the product detail page if needed
            )

            // Add to cart
            addToCart(cartItem)

            // Show a confirmation message
            Toast.makeText(this, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addToCart(cartItem: CartItem) {
        Cart.addToCart(cartItem)
        // You might want to add some way to notify the Cart fragment to update its view
    }
}