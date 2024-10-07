package com.example.clickcart.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clickcart.adapter.OrderItemAdapter
import com.example.clickcart.api.OrderApiService
import com.example.clickcart.api.RetrofitClient
import com.example.clickcart.databinding.ItemOrderView2Binding
import com.example.clickcart.models.Order
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class OrderDetailsFragment : Fragment() {
    private var _binding: ItemOrderView2Binding? = null
    private val binding get() = _binding!!
    private lateinit var orderItemAdapter: OrderItemAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ItemOrderView2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orderId = arguments?.getString("orderId") ?: return

        setupRecyclerView()
        fetchOrderDetails(orderId)

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        orderItemAdapter = OrderItemAdapter()
        binding.itemsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = orderItemAdapter
        }
    }

    private fun fetchOrderDetails(orderId: String) {
        val orderApiService = RetrofitClient.create().create(OrderApiService::class.java)
        orderApiService.getOrderDetails(orderId).enqueue(object : Callback<Order> {
            override fun onResponse(call: Call<Order>, response: Response<Order>) {
                if (response.isSuccessful) {
                    val order = response.body()
                    order?.let { updateUI(it) }
                }
            }

            override fun onFailure(call: Call<Order>, t: Throwable) {
                // Handle error
            }
        })
    }

    private fun updateUI(order: Order) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(order.createdAt)

        binding.apply {
            orderCreatedDate.text = date?.let { dateFormat.format(it) }
            orderTime.text = date?.let { timeFormat.format(it) }
            orderId.text = order.id
            orderStatus.text = order.orderStatus
        }

        orderItemAdapter.submitList(order.items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(orderId: String): OrderDetailsFragment {
            val fragment = OrderDetailsFragment()
            val args = Bundle()
            args.putString("orderId", orderId)
            fragment.arguments = args
            return fragment
        }
    }
}