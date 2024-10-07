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
import com.example.clickcart.adapters.ProductAdapter
import com.example.clickcart.api.ProductApiService
import com.example.clickcart.api.RetrofitClient
import com.example.clickcart.models.Product
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
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(this) // Pass 'this' as lifecycleOwner since Activity is a LifecycleOwner
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
            ratingOrder = SortOrder.NONE
            updateSortUI()
            filterAndSortProducts()
        }

        ratingSort.setOnClickListener {
            ratingOrder = when (ratingOrder) {
                SortOrder.NONE -> SortOrder.ASCENDING
                SortOrder.ASCENDING -> SortOrder.DESCENDING
                SortOrder.DESCENDING -> SortOrder.NONE
            }
            priceOrder = SortOrder.NONE
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
        var filteredProducts = allProducts.filter { product ->
            when (currentFilter) {
                FilterType.NAME -> product.name.contains(currentSearchText, ignoreCase = true)
                //FilterType.VENDOR -> product.vendor?.contains(currentSearchText, ignoreCase = true) ?: false
                else -> {// No filtering needed
                    true
                }
            }
        }

        // Sort by price
        when (priceOrder) {
            SortOrder.ASCENDING -> filteredProducts = filteredProducts.sortedBy { it.price }
            SortOrder.DESCENDING -> filteredProducts = filteredProducts.sortedByDescending { it.price }
            SortOrder.NONE -> { /* No sorting needed */ }
        }

        // Sort by rating
//        when (ratingOrder) {
//            SortOrder.ASCENDING -> filteredProducts = filteredProducts.sortedBy { it.rating }
//            SortOrder.DESCENDING -> filteredProducts = filteredProducts.sortedByDescending { it.rating }
//            SortOrder.NONE -> { /* No sorting needed */ }
//        }

        productAdapter.updateProducts(filteredProducts)
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
                        filterAndSortProducts()
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
}