package com.example.clickcart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.clickcart.models.Product
import com.example.clickcart.models.CartItem
import com.example.clickcart.fragment.Cart
import com.example.clickcart.data.VendorDataManager
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var product: Product
    private lateinit var addToCartButton: Button
    private lateinit var vendorName: TextView
    private lateinit var vendorId: TextView
    private lateinit var vendorRating: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // Initialize the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })

        product = intent.getSerializableExtra("PRODUCT") as Product

        initializeViews()
        setupAddToCartButton()
        loadVendorDetails()
    }

    private fun initializeViews() {
        // Existing views
        val productNameDetail: TextView = findViewById(R.id.productNameDetail)
        val productDescriptionDetail: TextView = findViewById(R.id.productDescriptionDetail)
        val productPriceDetail: TextView = findViewById(R.id.productPriceDetail)
        val productImageView: ImageView = findViewById(R.id.imageView)
        addToCartButton = findViewById(R.id.addToCartButton)

        // New vendor-related views
        vendorName = findViewById(R.id.store_name)
        vendorId = findViewById(R.id.store_status)
        vendorRating = findViewById(R.id.rating)  // Initialize this line

        // Set the product details
        productNameDetail.text = product.name
        productDescriptionDetail.text = product.description
        productPriceDetail.text = "$${product.price}"
        productImageView.setImageResource(R.drawable.product_img)

        // Initialize vendor views with loading state
        vendorName.text = "Loading..."
        vendorId.text = product.vendorId
        vendorRating.text = "..."  // This will now work correctly
    }

    private fun loadVendorDetails() {
        lifecycleScope.launch {
            try {
                val vendor = VendorDataManager.getVendorDetails(product.vendorId)
                vendorName.text = vendor.name

                val rating = VendorDataManager.getVendorRating(product.vendorId)
                vendorRating.text = String.format("%.1f", rating)
            } catch (e: Exception) {
                vendorName.text = "Unknown Vendor"
                vendorRating.text = "N/A"
                Toast.makeText(this@ProductDetailActivity, "Failed to load vendor details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupAddToCartButton() {
        addToCartButton.setOnClickListener {
            val cartItem = CartItem(
                productId = product.id,
                productName = product.name,
                productPrice = product.price,
                quantity = 1
            )
            addToCart(cartItem)
            Toast.makeText(this, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addToCart(cartItem: CartItem) {
        Cart.addToCart(cartItem)
    }
}