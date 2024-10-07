package com.example.clickcart.fragment

import android.content.Intent
import android.os.Bundle
import android.os.UserManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.clickcart.R
import com.example.clickcart.adapters.CartAdapter
import com.example.clickcart.api.OrderApiService
import com.example.clickcart.api.RetrofitClient
import com.example.clickcart.databinding.FragmentCartBinding
import com.example.clickcart.models.CartItem
import com.example.clickcart.models.OrderItem
import com.example.clickcart.models.OrderItemStatus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.navigation.fragment.findNavController
import com.example.clickcart.MainActivity
import com.example.clickcart.api.UserApiService
import com.example.clickcart.data.UserResponse
import com.example.clickcart.models.OrderStatus
import com.example.clickcart.models.Order
import com.example.clickcart.utils.TokenManager

class Cart : Fragment(), CartAdapter.OnQuantityChangeListener {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        updateCartContent()
        setupButtons()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(emptyList(), this)
        binding.cartRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }
    }

    private fun updateCartContent() {
        val currentCartItems = getCartItems()
        cartAdapter.updateItems(currentCartItems)
        updateCartVisibility(currentCartItems)
        updateCartSummary(currentCartItems)
    }

    private fun updateCartVisibility(items: List<CartItem>) {
        if (items.isEmpty()) {
            binding.noItemsTextView.visibility = View.VISIBLE
            binding.noItemsTextViewPara.visibility = View.VISIBLE
            binding.cartRecyclerView.visibility = View.GONE
            binding.cartSummaryLayout.visibility = View.GONE
            binding.cartActionButtonsLayout.visibility = View.GONE
        } else {
            binding.noItemsTextView.visibility = View.GONE
            binding.noItemsTextViewPara.visibility = View.GONE
            binding.cartRecyclerView.visibility = View.VISIBLE
            binding.cartSummaryLayout.visibility = View.VISIBLE
            binding.cartActionButtonsLayout.visibility = View.VISIBLE
        }
    }

    private fun updateCartSummary(items: List<CartItem>) {
        val total = items.sumOf { it.productPrice * it.quantity }
        binding.cartTotalAmount.text = getString(R.string.price_format, total)
    }

    private fun setupButtons() {
        binding.continueShoppingButton.setOnClickListener {
            navigateToHome()
        }

        binding.checkoutButton.setOnClickListener {
            createOrder()
        }
    }

    private fun navigateToHome() {
        val homeFragment = Home()

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, homeFragment)
            .addToBackStack(null)
            .commit()
    }


    override fun onQuantityChanged(item: CartItem, newQuantity: Int) {
        updateItemQuantity(item.productId, newQuantity)
        updateCartSummary(getCartItems())
    }

    override fun onResume() {
        super.onResume()
        updateCartContent()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val cartItems = mutableListOf<CartItem>()

        @JvmStatic
        fun addToCart(item: CartItem) {
            val existingItem = cartItems.find { it.productId == item.productId }
            if (existingItem != null) {
                existingItem.quantity += item.quantity
            } else {
                cartItems.add(item)
            }
        }

        @JvmStatic
        fun getCartItems(): List<CartItem> = cartItems.toList()

        @JvmStatic
        fun clearCart() {
            cartItems.clear()
        }

        @JvmStatic
        fun updateItemQuantity(productId: String, newQuantity: Int) {
            cartItems.find { it.productId == productId }?.let { item ->
                item.quantity = newQuantity
                if (item.quantity <= 0) {
                    cartItems.remove(item)
                }
            }
        }
    }

    private fun createOrder() {
//        val currentUser = UserManager.getCurrentUser()
//        if (currentUser == null) {
//            Toast.makeText(context, "Please log in to place an order", Toast.LENGTH_SHORT).show()
//            return
//        }

        val cartItems = getCartItems()
        if (cartItems.isEmpty()) {
            Toast.makeText(context, "Your cart is empty", Toast.LENGTH_SHORT).show()
            return
        }

        val orderItems = cartItems.map { cartItem ->
            OrderItem(
                productId = cartItem.productId,
//                productName = cartItem.productName,
                quantity = cartItem.quantity,
                price = cartItem.productPrice,
                totalPrice = cartItem.productPrice * cartItem.quantity,
//                status = OrderItemStatus.PENDING
            )
        }

        val totalPrice = orderItems.sumOf { it.totalPrice }
        var username = "";
        var uid = "";
//        var userId: String? = null
        val userId = TokenManager.getUserId()
            val service = RetrofitClient.create().create(UserApiService::class.java)
        if (userId != null) {
            service.getUserDetails(userId).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { userDetails ->
                            username = userDetails.name
                            uid = userDetails.id
                        } ?: run {
                            Toast.makeText(requireContext(), "User details not found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Error fetching user details", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Network error getUserDetails: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }


        val order = Order(
            customerId = userId.toString(),
//            customerName = username,
            items = orderItems,
            status = OrderStatus.PENDING.toString(),
            totalOrderPrice = totalPrice
        )
        Log.e("OrderApiService", "Order object: $order")
        val orderApiService = RetrofitClient.create().create(OrderApiService::class.java)
        orderApiService.createOrder(order).enqueue(object : Callback<Order> {
            override fun onResponse(call: Call<Order>, response: Response<Order>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("OrderApiService", "Response Body: $responseBody")

                    // Order created successfully
                    clearCart()
                    Toast.makeText(context, "Order placed successfully", Toast.LENGTH_SHORT).show()

                    // Navigate to the Home screen
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                } else {
                    // Log the error response body for debugging
                    Log.e("OrderApiService", "Error Response: ${response.errorBody()?.string()}")
                    Toast.makeText(context, "Failed to place order. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }


            override fun onFailure(call: Call<Order>, t: Throwable) {
                Log.e("OrderApiService", "Network error: ${t.message}", t)
                Toast.makeText(context, "Network error: ${t.message}. Please check your connection and try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}