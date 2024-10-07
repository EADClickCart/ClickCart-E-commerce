package com.example.clickcart.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clickcart.R
import com.example.clickcart.adapter.OrderAdapter
import com.example.clickcart.api.OrderApiService
import com.example.clickcart.api.ProductApiService
import com.example.clickcart.api.RetrofitClient
import com.example.clickcart.databinding.FragmentOrderBinding
import com.example.clickcart.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.clickcart.models.Order

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Order.newInstance] factory method to
 * create an instance of this fragment.
 */
class Order : Fragment() {
    private lateinit var binding: FragmentOrderBinding
    private lateinit var orderAdapter: OrderAdapter
    val retrofit = RetrofitClient.create()
    val orderApiService = retrofit.create(OrderApiService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        fetchOrders()
    }

    private fun setupRecyclerView() {
        orderAdapter = OrderAdapter(
            onReorderClick = { order ->
                Toast.makeText(context, "Reordering ${order.id}", Toast.LENGTH_SHORT).show()
            },
            onReportIssueClick = { order ->
                Toast.makeText(context, "Reporting issue for ${order.id}", Toast.LENGTH_SHORT).show()
            },
        )
        binding.cartRecyclerView.apply {
            adapter = orderAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun fetchOrders() {
        val customerId = TokenManager.getUserId()
        if (customerId != null) {
            orderApiService.getUserOrders(customerId).enqueue(object : Callback<List<Order>> {
                override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                    if (response.isSuccessful) {
                        val orders = response.body()
                        val responseBody = response.body()
                        Log.d("OrderApiService", "Response Body: $responseBody")
                        if (orders.isNullOrEmpty()) {
                            showEmptyState()
                        } else {
                            showOrders(orders)
                        }
                    } else {
                        // Handle error
                        showEmptyState()
                    }
                }

                override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                    // Handle network error
                    Log.e("OrderApiService", "Network error: ${t.message}", t)
                    showEmptyState()
                }
            })
        }
    }

    private fun showEmptyState() {
        binding.apply {
            noItemsTextView.visibility = View.VISIBLE
            noItemsTextViewPara.visibility = View.VISIBLE
            cartRecyclerView.visibility = View.GONE
        }
    }

    private fun showOrders(orders: List<Order>) {
        binding.apply {
            noItemsTextView.visibility = View.GONE
            noItemsTextViewPara.visibility = View.GONE
            cartRecyclerView.visibility = View.VISIBLE
        }
        orderAdapter.submitList(orders)
    }
}

