package com.example.clickcart.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clickcart.R
import com.example.clickcart.models.CartItem

class CartAdapter(
    private var cartItems: List<CartItem>,
    private val onQuantityChangeListener: OnQuantityChangeListener
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    interface OnQuantityChangeListener {
        fun onQuantityChanged(item: CartItem, newQuantity: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.bind(cartItem)
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateItems(newItems: List<CartItem>) {
        cartItems = newItems
        notifyDataSetChanged()
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.cartProductName)
        private val productPrice: TextView = itemView.findViewById(R.id.cartProductPrice)
        private val quantity: TextView = itemView.findViewById(R.id.cartProductQuantity)
        private val totalPrice: TextView = itemView.findViewById(R.id.totalPriceForItems)
        private val minusButton: TextView = itemView.findViewById(R.id.textViewMinus)
        private val plusButton: TextView = itemView.findViewById(R.id.textViewPlus)

        fun bind(cartItem: CartItem) {
            productName.text = cartItem.productName
            productPrice.text = itemView.context.getString(R.string.price_format, cartItem.productPrice)
            quantity.text = cartItem.quantity.toString()
            updateTotalPrice(cartItem)

            minusButton.setOnClickListener {
                if (cartItem.quantity > 1) {
                    val newQuantity = cartItem.quantity - 1
                    onQuantityChangeListener.onQuantityChanged(cartItem, newQuantity)
                    quantity.text = newQuantity.toString()
                    updateTotalPrice(cartItem.copy(quantity = newQuantity))
                }
            }

            plusButton.setOnClickListener {
                val newQuantity = cartItem.quantity + 1
                onQuantityChangeListener.onQuantityChanged(cartItem, newQuantity)
                quantity.text = newQuantity.toString()
                updateTotalPrice(cartItem.copy(quantity = newQuantity))
            }
        }

        private fun updateTotalPrice(cartItem: CartItem) {
            val total = cartItem.productPrice * cartItem.quantity
            totalPrice.text = itemView.context.getString(R.string.price_format, total)
        }
    }
}