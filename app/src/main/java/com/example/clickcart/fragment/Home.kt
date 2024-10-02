package com.example.clickcart.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clickcart.R
import com.example.clickcart.SearchActivity
import com.example.clickcart.adapters.CategoryAdapter
import com.example.clickcart.adapters.ProductAdapter
import com.example.clickcart.api.CategoryApiService
import com.example.clickcart.api.ProductApiService
import com.example.clickcart.models.Category
import com.example.clickcart.models.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Home : Fragment() {

    // For categories
    private lateinit var categoriesRecyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter

    // For products
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter

    private lateinit var searchBar: TextView  // Declare the searchBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize RecyclerView for categories
        categoriesRecyclerView = view.findViewById(R.id.categoriesRecyclerView)
        categoryAdapter = CategoryAdapter()
        categoriesRecyclerView.adapter = categoryAdapter
        categoriesRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Initialize RecyclerView for products
        productsRecyclerView = view.findViewById(R.id.featuredProductsRecyclerView)
        productAdapter = ProductAdapter()
        productsRecyclerView.adapter = productAdapter
        productsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        // Initialize searchBar and set click listener
        searchBar = view.findViewById(R.id.searchBar)
        searchBar.setOnClickListener {
            // Open the search activity
            openSearchScreen()
        }

        // Fetch categories
        fetchCategories()

        // Fetch products
        fetchProducts()

        return view
    }

    private fun fetchCategories() {
        // Use Retrofit to fetch categories from your API
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5296/")  // Use this for emulator, replace with actual URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(CategoryApiService::class.java)
        val call = service.getCategories()

        call.enqueue(object : Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                if (response.isSuccessful) {
                    response.body()?.let { categories ->
                        categoryAdapter.updateCategories(categories)
                    }
                } else {
                    Toast.makeText(requireContext(), "Error fetching categories", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchProducts() {
        // Use Retrofit to fetch products from your API
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5296/")  // Use this for emulator, replace with actual URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ProductApiService::class.java)
        val call = service.getProducts()

        call.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    response.body()?.let { products ->
                        productAdapter.updateProducts(products)
                    }
                } else {
                    Toast.makeText(requireContext(), "Error fetching products", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                t.printStackTrace()
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Method to open the search screen
    private fun openSearchScreen() {
        val intent = Intent(requireContext(), SearchActivity::class.java)
        startActivity(intent)
    }
}
