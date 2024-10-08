package com.example.clickcart

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clickcart.adapters.VendorReviewAdapter
import com.example.clickcart.api.RetrofitClient
import com.example.clickcart.api.VendorApiService
import com.example.clickcart.viewmodels.VendorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VendorDetailActivity : AppCompatActivity() {
    private lateinit var reviewsRecyclerView: RecyclerView
    private lateinit var reviewsAdapter: VendorReviewAdapter
    private lateinit var noReviewsLayout: LinearLayout
    private val viewModel: VendorViewModel by viewModels()
    private val vendorApi = RetrofitClient.create().create(VendorApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.vendor_view)

        setupViews()
        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        val vendorId = intent.getStringExtra("VENDOR_ID") ?: return
        fetchVendorReviews(vendorId)
    }

    private fun setupViews() {
        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView)
        noReviewsLayout = findViewById(R.id.noReviewsLayout)
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        reviewsAdapter = VendorReviewAdapter()
        reviewsRecyclerView.apply {
            adapter = reviewsAdapter
            layoutManager = LinearLayoutManager(this@VendorDetailActivity)
        }
    }

    private fun observeViewModel() {
        viewModel.reviews.observe(this) { reviews ->
            if (reviews.isEmpty()) {
                reviewsRecyclerView.visibility = View.GONE
                noReviewsLayout.visibility = View.VISIBLE
            } else {
                reviewsRecyclerView.visibility = View.VISIBLE
                noReviewsLayout.visibility = View.GONE
                reviewsAdapter.updateReviews(reviews)
            }
        }
    }

    private fun fetchVendorReviews(vendorId: String) {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    vendorApi.getVendorReviews(vendorId)
                }

                if (response.isSuccessful) {
                    response.body()?.let { reviews ->
                        viewModel.setReviews(reviews)
                    }
                } else {
                    Toast.makeText(this@VendorDetailActivity, "Error: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@VendorDetailActivity, "Error fetching reviews: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}