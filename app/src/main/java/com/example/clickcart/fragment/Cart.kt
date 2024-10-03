package com.example.clickcart.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.clickcart.R
import com.example.clickcart.databinding.FragmentCartBinding

class Cart : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val cartItems = mutableListOf<String>() // Replace String with your actual cart item type

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Populate cartItems with actual data
        // For now, we'll just check if it's empty

        updateCartVisibility()
    }

    private fun updateCartVisibility() {
        if (cartItems.isEmpty()) {
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

            // Set up RecyclerView adapter and populate with cartItems
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = Cart()
    }
}