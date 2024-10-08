package com.example.clickcart

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.clickcart.adapters.ProductAdapter
import com.example.clickcart.api.ProductApiService
import com.example.clickcart.api.RetrofitClient
import com.example.clickcart.data.VendorDataManager
import com.example.clickcart.models.Product
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var searchBar: EditText
    private lateinit var filterName: TextView
    private lateinit var filterVendor: TextView
    private lateinit var ratingSort: TextView
    private lateinit var priceSort: TextView
    private lateinit var ratingSortIcon: ImageView
    private lateinit var priceSortIcon: ImageView
    private lateinit var noProductAvailable: TextView


    private var allProducts: List<Product> = listOf()
    private var currentSearchText: String = ""
    private var currentFilter: FilterType = FilterType.NAME
    private var priceOrder: SortOrder = SortOrder.NONE
    private var ratingOrder: SortOrder = SortOrder.NONE

    enum class FilterType {
        NAME, VENDOR
    }

    enum class SortOrder {
        ASCENDING, DESCENDING, NONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initializeViews()
        setupRecyclerView()
        setupListeners()
        fetchProducts()
    }

    private fun initializeViews() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        searchBar = findViewById(R.id.searchBar)
        filterName = findViewById(R.id.filter_name)
        filterVendor = findViewById(R.id.filter_vendor)
        ratingSort = findViewById(R.id.rating)
        priceSort = findViewById(R.id.price)
        ratingSortIcon = findViewById(R.id.rating_short_icon)
        priceSortIcon = findViewById(R.id.price_short_icon)
        productsRecyclerView = findViewById(R.id.recyclerView)
        noProductAvailable = findViewById(R.id.noProductAvailable)

    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(this) // 'this' is now correct as it's passed as a LifecycleOwner
        productsRecyclerView.apply {
            adapter = productAdapter
            layoutManager = GridLayoutManager(this@SearchActivity, 2)
        }

        productAdapter.setOnItemClickListener { product ->
            val intent = Intent(this, ProductDetailActivity::class.java)
            intent.putExtra("PRODUCT", product)
            startActivity(intent)
        }
    }

    private fun setupListeners() {
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                currentSearchText = s.toString()
                filterAndSortProducts()
            }
        })

        filterName.setOnClickListener {
            currentFilter = FilterType.NAME
            updateFilterUI()
            filterAndSortProducts()
        }

        filterVendor.setOnClickListener {
            currentFilter = FilterType.VENDOR
            updateFilterUI()
            filterAndSortProducts()
        }

        priceSort.setOnClickListener {
            priceOrder = when (priceOrder) {
                SortOrder.NONE -> SortOrder.ASCENDING
                SortOrder.ASCENDING -> SortOrder.DESCENDING
                SortOrder.DESCENDING -> SortOrder.NONE
            }
            //ratingOrder = SortOrder.NONE
            updateSortUI()
            filterAndSortProducts()
        }

        ratingSort.setOnClickListener {
            ratingOrder = when (ratingOrder) {
                SortOrder.NONE -> SortOrder.ASCENDING
                SortOrder.ASCENDING -> SortOrder.DESCENDING
                SortOrder.DESCENDING -> SortOrder.NONE
            }
            //priceOrder = SortOrder.NONE
            updateSortUI()
            filterAndSortProducts()
        }
    }

    private fun updateFilterUI() {
        filterName.setTextColor(getColor(if (currentFilter == FilterType.NAME) R.color.purple_500 else R.color.black))
        filterVendor.setTextColor(getColor(if (currentFilter == FilterType.VENDOR) R.color.purple_500 else R.color.black))
    }

    private fun updateSortUI() {
        priceSortIcon.setImageResource(
            when (priceOrder) {
                SortOrder.ASCENDING -> R.drawable.short_up
                SortOrder.DESCENDING -> R.drawable.short_down
                SortOrder.NONE -> R.drawable.short_none
            }
        )
        ratingSortIcon.setImageResource(
            when (ratingOrder) {
                SortOrder.ASCENDING -> R.drawable.short_up
                SortOrder.DESCENDING -> R.drawable.short_down
                SortOrder.NONE -> R.drawable.short_none
            }
        )
    }

    private fun filterAndSortProducts() {
        lifecycleScope.launch {
            var filteredProducts = allProducts.filter { product ->
                when (currentFilter) {
                    FilterType.NAME -> product.name.contains(currentSearchText, ignoreCase = true)
                    FilterType.VENDOR -> product.vendorName?.contains(currentSearchText, ignoreCase = true) ?: false
                }
            }

            // Apply both sorts if needed
            filteredProducts = when {
                // Both price and rating are active
                priceOrder != SortOrder.NONE && ratingOrder != SortOrder.NONE -> {
                    filteredProducts.sortedWith(
                        compareBy<Product> {
                            when (priceOrder) {
                                SortOrder.ASCENDING -> it.price
                                SortOrder.DESCENDING -> -it.price
                                SortOrder.NONE -> 0.0
                            }
                        }.thenBy {
                            when (ratingOrder) {
                                SortOrder.ASCENDING -> it.rating ?: 0.0
                                SortOrder.DESCENDING -> -(it.rating ?: 0.0)
                                SortOrder.NONE -> 0.0
                            }
                        }
                    )
                }
                // Only price sort is active
                priceOrder != SortOrder.NONE -> {
                    when (priceOrder) {
                        SortOrder.ASCENDING -> filteredProducts.sortedBy { it.price }
                        SortOrder.DESCENDING -> filteredProducts.sortedByDescending { it.price }
                        SortOrder.NONE -> filteredProducts
                    }
                }
                // Only rating sort is active
                ratingOrder != SortOrder.NONE -> {
                    when (ratingOrder) {
                        SortOrder.ASCENDING -> filteredProducts.sortedBy { it.rating ?: 0.0 }
                        SortOrder.DESCENDING -> filteredProducts.sortedByDescending { it.rating ?: 0.0 }
                        SortOrder.NONE -> filteredProducts
                    }
                }
                // No sorting needed
                else -> filteredProducts
            }

            // Update UI visibility
            if (filteredProducts.isEmpty()) {
                noProductAvailable.visibility = TextView.VISIBLE
            } else {
                noProductAvailable.visibility = TextView.GONE
            }

            productAdapter.updateProducts(filteredProducts)
        }
    }


    private fun fetchProducts() {
        val retrofit = RetrofitClient.create()
        val service = retrofit.create(ProductApiService::class.java)
        val call = service.getProducts()

        call.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    response.body()?.let { products ->
                        allProducts = products
                        lifecycleScope.launch {
                            fetchVendorDetailsAndRatings()
                            filterAndSortProducts()
                        }
                    }
                } else {
                    Toast.makeText(this@SearchActivity, "Error fetching products", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@SearchActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private suspend fun fetchVendorDetailsAndRatings() {
        allProducts.forEach { product ->
            try {
                val vendor = VendorDataManager.getVendorDetails(product.vendorId)
                product.vendorName = vendor.name

                val vendorRating = VendorDataManager.getVendorRating(product.vendorId)
                product.rating = vendorRating
            } catch (e: Exception) {
                product.vendorName = "Unknown Vendor"
                product.rating = null
            }
        }
    }
}