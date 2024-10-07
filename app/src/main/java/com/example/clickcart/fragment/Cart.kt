package com.example.clickcart.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.example.clickcart.MainActivity
import com.example.clickcart.models.OrderStatus
import com.example.clickcart.models.Order
import com.example.clickcart.utils.TokenManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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

        loadCartFromLocalStorage()
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

    private fun loadCartFromLocalStorage() {
        val sharedPreferences = requireContext().getSharedPreferences("cart_preferences", Context.MODE_PRIVATE)
        val cartJson = sharedPreferences.getString("cart_items", null)

        if (cartJson != null) {
            val itemType = object : TypeToken<MutableList<CartItem>>() {}.type
            val loadedCartItems: MutableList<CartItem> = Gson().fromJson(cartJson, itemType)
            cartItems.clear()
            cartItems.addAll(loadedCartItems)
        }
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
        updateItemQuantity(item.productId, newQuantity, requireContext())
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
        fun addToCart(item: CartItem, context: Context) {
            val sharedPreferences = context.getSharedPreferences("cart_preferences", Context.MODE_PRIVATE)
            val existingItem = cartItems.find { it.productId == item.productId }
            if (existingItem != null) {
                existingItem.quantity += item.quantity
            } else {
                cartItems.add(item)
            }
            saveCartToLocalStorage(context)
        }

        @JvmStatic
        fun getCartItems(): List<CartItem> = cartItems.toList()

        @JvmStatic
        fun clearCart(context: Context) {
            cartItems.clear()
            saveCartToLocalStorage(context)
        }

        @JvmStatic
        fun updateItemQuantity(productId: String, newQuantity: Int, context: Context) {
            cartItems.find { it.productId == productId }?.let { item ->
                item.quantity = newQuantity
                if (item.quantity <= 0) {
                    cartItems.remove(item)
                }
                saveCartToLocalStorage(context)
            }
        }

        @JvmStatic
        private fun saveCartToLocalStorage(context: Context) {
            val sharedPreferences = context.getSharedPreferences("cart_preferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()

            val cartJson = Gson().toJson(cartItems)
            editor.putString("cart_items", cartJson)
            editor.apply()
        }
    }

    private fun createOrder() {

        val cartItems = getCartItems()
        if (cartItems.isEmpty()) {
            Toast.makeText(context, "Your cart is empty", Toast.LENGTH_SHORT).show()
            return
        }

        val orderItems = cartItems.map { cartItem ->
            OrderItem(
                productId = cartItem.productId,
                productName = cartItem.productName,
                quantity = cartItem.quantity,
                price = cartItem.productPrice,
                totalPrice = cartItem.productPrice * cartItem.quantity,
                status = OrderItemStatus.PURCHASED.toString(),
                vendorId = cartItem.vendorId,
                vendorName = cartItem.vendorName
            )
        }

        val totalPrice = orderItems.sumOf { it.totalPrice }
        val userId = TokenManager.getUserId()


        val order = Order(
            customerId = userId.toString(),
            items = orderItems,
            orderStatus = OrderStatus.PURCHASED.toString(),
            totalOrderPrice = totalPrice
        )
        Log.e("OrderApiService", "Order object: $order")
        val orderApiService = RetrofitClient.create().create(OrderApiService::class.java)
        orderApiService.createOrder(order).enqueue(object : Callback<Order> {
            override fun onResponse(call: Call<Order>, response: Response<Order>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("OrderApiService", "Response Body: $responseBody")

                    clearCart(requireContext())
                    Toast.makeText(context, "Order placed successfully", Toast.LENGTH_SHORT).show()

                    val sharedPreferences = requireContext().getSharedPreferences("cart_preferences", Context.MODE_PRIVATE)
                    sharedPreferences.edit().clear().apply()
                    
                    val intent = Intent(requireActivity(), MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                } else {
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